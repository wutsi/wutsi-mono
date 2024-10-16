package com.wutsi.blog.user.service

import com.wutsi.blog.account.domain.SessionEntity
import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.EventType.BLOG_CREATED_EVENT
import com.wutsi.blog.event.EventType.USER_ACTIVATED_EVENT
import com.wutsi.blog.event.EventType.USER_ATTRIBUTE_UPDATED_EVENT
import com.wutsi.blog.event.EventType.USER_DEACTIVATED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.service.sender.blog.WelcomeBloggerMailSender
import com.wutsi.blog.product.dao.StoreRepository
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.transaction.dao.SuperFanRepository
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dao.WalletRepository
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.dao.SearchUserQueryBuilder
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.dto.ActivateUserCommand
import com.wutsi.blog.user.dto.BlogCreateEventPayload
import com.wutsi.blog.user.dto.CreateBlogCommand
import com.wutsi.blog.user.dto.DeactivateUserCommand
import com.wutsi.blog.user.dto.JoinWPPCommand
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UpdateUserAttributeCommand
import com.wutsi.blog.user.dto.UserAttributeUpdatedEvent
import com.wutsi.blog.util.Predicates
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.payment.core.Status
import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.time.Clock
import java.util.Date
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val dao: UserRepository,
    private val storyDao: StoryRepository,
    private val transactionDao: TransactionRepository,
    private val superFanDao: SuperFanRepository,
    private val walletDao: WalletRepository,
    private val storeDao: StoreRepository,
    private val clock: Clock,
    private val logger: KVLogger,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val em: EntityManager,
    private val storage: StorageService,
    private val welcomeBloggerMailSender: WelcomeBloggerMailSender,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UserService::class.java)
    }

    fun findById(id: Long): UserEntity =
        validate(
            dao
                .findById(id)
                .orElseThrow { NotFoundException(Error(ErrorCode.USER_NOT_FOUND)) },
        )

    fun findByIds(ids: List<Long>): List<UserEntity> =
        dao.findAllById(ids).filter { !it.suspended }

    fun findByName(name: String): UserEntity =
        validate(
            dao
                .findByNameIgnoreCase(name.lowercase())
                .orElseThrow { NotFoundException(Error(ErrorCode.USER_NOT_FOUND)) },
        )

    fun findByEmail(email: String): UserEntity =
        validate(
            dao
                .findByEmailIgnoreCase(email)
                .orElseThrow {
                    NotFoundException(Error(ErrorCode.USER_NOT_FOUND))
                },
        )

    @Transactional
    fun findByEmailOrCreate(email: String, country: String? = null, fullName: String = ""): UserEntity {
        val i = email.indexOf("@")
        val providerUserId = if (i > 0) {
            email.substring(0, i)
        } else {
            email
        }

        return dao.findByEmailIgnoreCase(email).getOrNull()
            ?: dao.save(
                UserEntity(
                    email = email.lowercase(),
                    name = generateName(email, providerUserId),
                    country = country?.lowercase(),
                    fullName = fullName
                ),
            )
    }

    @Transactional
    fun onWeeklyEmailSent(user: UserEntity) {
        val now = Date()
        user.lastWeeklyEmailSentDateTime = now
        user.modificationDateTime = now
        dao.save(user)
    }

    @Transactional
    fun onWeeklyEmailOpened(userId: Long, timestamp: Long) {
        val user = findById(userId)
        user.lastWeeklyEmailOpenedDateTime = Date(timestamp)
        user.modificationDateTime = Date()
        dao.save(user)
    }

    private fun validate(user: UserEntity): UserEntity {
        if (user.suspended) {
            throw NotFoundException(Error(ErrorCode.USER_SUSPENDED))
        }
        return user
    }

    fun search(request: SearchUserRequest): List<UserEntity> {
        val builder = SearchUserQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, UserEntity::class.java)
        Predicates.setParameters(query, params)
        return query.resultList as List<UserEntity>
    }

    @Transactional
    fun onWalletCreated(user: UserEntity, wallet: WalletEntity) {
        user.walletId = wallet.id
        if (user.country == null) {
            user.country = wallet.country
        }
        user.modificationDateTime = Date()
        dao.save(user)
    }

    @Transactional
    fun onStoreCreated(user: UserEntity, store: StoreEntity) {
        user.storeId = store.id
        user.modificationDateTime = Date()
        dao.save(user)
    }

    @Transactional
    fun onTransactionSuccesfull(user: UserEntity) {
        val wallet = user.walletId?.let { walletId -> walletDao.findById(walletId).getOrNull() }
        if (wallet != null) {
            user.donationCount =
                transactionDao.countByWalletAndTypeAndStatus(wallet, TransactionType.DONATION, Status.SUCCESSFUL) ?: 0
            user.superFanCount = superFanDao.countByWalletId(wallet.id ?: "-") ?: 0
        }

        val store = user.storeId?.let { storeId -> storeDao.findById(storeId).getOrNull() }
        if (store != null) {
            user.orderCount =
                transactionDao.countByStoreAndTypeAndStatus(store, TransactionType.CHARGE, Status.SUCCESSFUL) ?: 0

            user.totalSales =
                transactionDao.sumAmountByStoreAndTypeAndStatus(store, TransactionType.CHARGE, Status.SUCCESSFUL) ?: 0
        }

        if (wallet != null || store != null) {
            user.modificationDateTime = Date()
            dao.save(user)
        }
    }

    @Transactional
    fun onLoggedIn(session: SessionEntity) {
        val user = findById(session.account.user.id!!)
        user.lastLoginDateTime = session.loginDateTime
        try {
            user.pictureUrl = downloadImage(user)
        } catch (ex: Exception) {
            logger.setException(ex)
        }

        dao.save(user)
    }

    fun downloadImage(user: UserEntity): String? {
        if (user.pictureUrl.isNullOrBlank()) {
            return null
        }

        val url = URL(user.pictureUrl)
        logger.add("picture_url", url)
        if (storage.contains(url)) {
            return user.pictureUrl
        }

        // Download
        val img = ImageIO.read(url)
        val file = File.createTempFile(UUID.randomUUID().toString(), ".png")
        val out = FileOutputStream(file)
        try {
            ImageIO.write(img, "png", out)

            // Store
            val input = FileInputStream(file)
            input.use {
                val xurl = storage.store("user/${user.id}/${file.name}", input)
                logger.add("picture_local_url", xurl)
                return xurl.toString()
            }
        } finally {
            out.close()
        }
    }

    @Transactional
    fun activate(command: ActivateUserCommand) {
        if (activate(command.userId, true)) {
            notify(command.userId, USER_ACTIVATED_EVENT, command.timestamp)
        }
    }

    @Transactional
    fun deactivate(command: DeactivateUserCommand) {
        if (activate(command.userId, false)) {
            notify(command.userId, USER_DEACTIVATED_EVENT, command.timestamp)
        }
    }

    private fun activate(userId: Long, active: Boolean): Boolean {
        val user = findById(userId)
        return if (user.active != active) {
            user.active = active
            user.lastPublicationDateTime = Date()
            dao.save(user)

            true
        } else {
            false
        }
    }

    private fun notify(userId: Long, type: String, timestamp: Long) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.USER,
                entityId = userId.toString(),
                type = type,
                timestamp = Date(timestamp),
            ),
        )

        val payload = EventPayload(eventId)
        eventStream.publish(type, payload)
    }

    @Transactional
    fun onStoryCreated(story: StoryEntity) {
        val user = dao.findById(story.userId).get()
        updateStoryCount(user)
        user.modificationDateTime = Date()
        dao.save(user)
    }

    @Transactional
    fun onStoryDeleted(story: StoryEntity) {
        val user = dao.findById(story.userId).get()
        updateStoryCount(user)
        user.modificationDateTime = Date()
        dao.save(user)
    }

    @Transactional
    fun onStoryPublished(story: StoryEntity) {
        val user = dao.findById(story.userId).get()
        updateStoryCount(user)
        user.lastPublicationDateTime = story.publishedDateTime
        user.modificationDateTime = Date()
        user.active = true
        dao.save(user)
    }

    @Transactional
    fun onStoryUnpublished(story: StoryEntity) {
        val user = dao.findById(story.userId).get()
        updateStoryCount(user)
        user.modificationDateTime = Date()
        dao.save(user)
    }

    @Transactional
    fun onKpisImported(user: UserEntity) {
        user.readCount = storyDao.sumReadCountByUserId(user.id!!) ?: 0
        user.clickCount = storyDao.averageClickCountByUserId(user.id) ?: 0
        user.totalDurationSeconds = storyDao.sumTotalDurationSecondsByUserId(user.id) ?: 0
        user.modificationDateTime = Date()
        dao.save(user)
    }

    @Transactional
    fun pin(story: StoryEntity, timestamp: Long): Boolean {
        val user = dao.findById(story.userId).get()
        if (user.pinStoryId == story.id) {
            return false
        }

        user.pinStoryId = story.id
        user.pinDateTime = Date(timestamp)
        user.modificationDateTime = Date()
        dao.save(user)
        return true
    }

    @Transactional
    fun unpin(story: StoryEntity): Boolean {
        val user = dao.findById(story.userId).get()
        if (user.pinStoryId == null) {
            return false
        }

        user.pinStoryId = null
        user.pinDateTime = null
        user.modificationDateTime = Date()
        dao.save(user)
        return true
    }

    @Transactional
    fun onSubscribed(user: UserEntity) {
        updateSubscriberCount(user)
    }

    @Transactional
    fun onUnsubscribed(user: UserEntity) {
        updateSubscriberCount(user)
    }

    @Transactional
    fun onUserEndoresed(userId: Long) {
        dao.findById(userId).map { user ->
            updateEndorserCount(user)
        }
    }

    private fun updateSubscriberCount(user: UserEntity) {
        val subscriptions = count(StreamId.SUBSCRIPTION, user, EventType.SUBSCRIBED_EVENT)
        val unsubscriptions = count(StreamId.SUBSCRIPTION, user, EventType.UNSUBSCRIBED_EVENT)
        user.subscriberCount = Math.max(0, subscriptions - unsubscriptions)
        user.modificationDateTime = Date()
        dao.save(user)
    }

    private fun updateEndorserCount(user: UserEntity) {
        user.endorserCount = count(StreamId.ENDORSEMENT, user, EventType.USER_ENDORSED_EVENT)
        user.modificationDateTime = Date()
        dao.save(user)
    }

    private fun updateStoryCount(user: UserEntity) {
        user.draftStoryCount = storyDao.countByUserIdAndStatusAndDeleted(user.id!!, StoryStatus.DRAFT, false)
        user.publishStoryCount = storyDao.countByUserIdAndStatusAndDeleted(user.id, StoryStatus.PUBLISHED, false)
        user.storyCount = user.draftStoryCount + user.publishStoryCount
    }

    private fun count(streamId: Long, user: UserEntity, type: String): Long =
        eventStore.eventCount(streamId = streamId, entityId = user.id.toString(), type = type)

    @Transactional
    fun createBlog(command: CreateBlogCommand) {
        logger.add("command", "CreateBlogCommand")
        logger.add("command_user_id", command.userId)

        val user = findById(command.userId)
        if (!user.blog) {
            user.blog = true
            user.blogDateTime = Date()
            user.modificationDateTime = Date()
            save(user)
            notify(BLOG_CREATED_EVENT, command.userId, BlogCreateEventPayload(command.subscribeToUserIds))
        }
    }

    fun onBlogCreated(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)

        // blogs to subscribe to
        val subscriberUserIds = mutableListOf<Long>()
        val data = event.payload
        if (data is BlogCreateEventPayload) {
            subscriberUserIds.addAll(data.subscribeToUserIds)
        }

        // Subscribe
        if (subscriberUserIds.isNotEmpty()) {
            val subscriberId = event.entityId.toLong()
            subscriberUserIds.toSet().forEach { userId ->
                eventStream.enqueue(
                    type = EventType.SUBSCRIBE_COMMAND,
                    payload = SubscribeCommand(
                        userId = userId,
                        subscriberId = subscriberId,
                        timestamp = event.timestamp.time,
                        referer = "create-blog",
                    )
                )
            }
        }

        // Send email
        event.entityId.let { id ->
            try {
                val blog = dao.findById(id.toLong()).get()
                welcomeBloggerMailSender.send(blog)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to send welcome email to User#$id", ex)
            }
        }
    }

    @Transactional
    fun updateAttribute(command: UpdateUserAttributeCommand) {
        logger.add("command_user_id", command.userId)
        logger.add("command_name", command.name)
        logger.add("command_value", command.value)

        set(command.userId, command.name, command.value)

        val payload = UserAttributeUpdatedEvent(
            name = command.name,
            value = command.value,
        )
        notify(USER_ATTRIBUTE_UPDATED_EVENT, command.userId, payload)
    }

    @Transactional
    fun joinWPP(command: JoinWPPCommand) {
        logger.add("command_user_id", command.userId)
        val user = findById(command.userId)
        if (!user.wpp) {
            user.wpp = true
            user.wppDateTime = Date()
            save(user)
        }
    }

    private fun set(id: Long, name: String, value: String?): UserEntity {
        val user = findById(id)
        val lname = name.lowercase()

        if ("name" == lname) {
            rename(user, value!!)
        } else if ("email" == lname) {
            updateEmail(user, value!!)
        } else if ("full_name" == lname) {
            user.fullName = value ?: ""
        } else if ("biography" == lname) {
            user.biography = value
        } else if ("picture_url" == lname) {
            user.pictureUrl = value
        } else if ("website_url" == lname) {
            user.websiteUrl = value
        } else if ("language" == lname) {
            user.language = value
        } else if ("read_all_languages" == lname) {
            user.readAllLanguages = ("true" == value!!)
        } else if ("facebook_id" == lname) {
            user.facebookId = value
        } else if ("twitter_id" == lname) {
            user.twitterId = value
        } else if ("linkedin_id" == lname) {
            user.linkedinId = value
        } else if ("youtube_id" == lname) {
            user.youtubeId = value
        } else if ("github_id" == lname) {
            user.githubId = value
        } else if ("whatsapp_id" == lname) {
            user.whatsappId = value
        } else if ("telegram_id" == lname) {
            user.telegramId = value
        } else if ("country" == lname) {
            user.country = value?.lowercase()
        } else {
            throw ConflictException(Error(ErrorCode.USER_ATTRIBUTE_INVALID))
        }

        user.modificationDateTime = Date(clock.millis())
        return save(user)
    }

    private fun save(user: UserEntity): UserEntity {
        user.modificationDateTime = Date(clock.millis())
        return dao.save(user)
    }

    private fun rename(user: UserEntity, value: String) {
        checkNameUnique(user, value)
        user.name = value.lowercase()
    }

    private fun updateEmail(user: UserEntity, email: String) {
        checkEmailUnique(user, email)
        user.email = email
    }

    private fun checkNameUnique(user: UserEntity, name: String) {
        val dup = dao.findByNameIgnoreCase(name)
        if (dup.isPresent && dup.get().id != user.id) {
            throw ConflictException(Error(ErrorCode.USER_NAME_DUPLICATE))
        }
    }

    private fun checkEmailUnique(user: UserEntity, name: String) {
        val dup = dao.findByEmailIgnoreCase(name)
        if (dup.isPresent && dup.get().id != user.id) {
            throw ConflictException(Error(ErrorCode.USER_EMAIL_DUPLICATE))
        }
    }

    @Transactional
    fun createUser(
        fullName: String,
        email: String?,
        providerUserId: String,
        pictureUrl: String?,
        language: String?,
        country: String?,
    ): UserEntity {
        val name = generateName(email, providerUserId)

        val user = UserEntity(
            fullName = fullName,
            email = email?.lowercase(),
            pictureUrl = pictureUrl,
            name = name,
            language = language,
            country = country,
        )
        return save(user)
    }

    private fun generateName(email: String?, providerUserId: String): String {
        var name = email?.let { extractNameFromEmail(email) }
        if (name == null) {
            name = extractNameFromProviderId(providerUserId)
        }
        return name ?: providerUserId + "-" + System.currentTimeMillis()
    }

    private fun extractNameFromEmail(email: String): String? {
        val i = email.indexOf("@")
        val name = email.substring(0, i)

        val user = dao.findByNameIgnoreCase(name)
        return if (user.isPresent) {
            null
        } else {
            name
        }
    }

    private fun extractNameFromProviderId(providerUserId: String): String? {
        val user = dao.findByNameIgnoreCase(providerUserId)
        return if (user.isPresent) {
            null
        } else {
            providerUserId
        }
    }

    private fun notify(type: String, userId: Long, payload: Any? = null) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.USER,
                type = type,
                entityId = userId.toString(),
                payload = payload,
            ),
        )
        logger.add("evt_id", eventId)

        eventStream.enqueue(type, EventPayload(eventId = eventId))
        eventStream.publish(type, EventPayload(eventId = eventId))
    }
}
