package com.wutsi.blog.mail.service

import com.wutsi.blog.SortOrder
import com.wutsi.blog.event.EventType
import com.wutsi.blog.mail.dto.SendStoryDailyEmailCommand
import com.wutsi.blog.mail.service.sender.blog.WelcomeSubscriberMailSender
import com.wutsi.blog.mail.service.sender.product.EBookLaunchMailSender
import com.wutsi.blog.mail.service.sender.story.DailyMailSender
import com.wutsi.blog.mail.service.sender.story.WeeklyMailSender
import com.wutsi.blog.mail.service.sender.transaction.OrderAbandonedMailSender
import com.wutsi.blog.mail.service.sender.transaction.OrderMailSender
import com.wutsi.blog.product.dao.StoreRepository
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchProductContext
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.ProductService
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.payment.core.Status
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.jvm.optionals.getOrNull

@Service
class MailService(
    private val storyService: StoryService,
    private val logger: KVLogger,
    private val userDao: UserRepository,
    private val storeDao: StoreRepository,
    private val xemailService: XEmailService,
    private val subscriptionDao: SubscriptionRepository,
    private val productService: ProductService,
    private val dailyMailSender: DailyMailSender,
    private val weeklyMailSender: WeeklyMailSender,
    private val orderMailSender: OrderMailSender,
    private val abandonedMailSender: OrderAbandonedMailSender,
    private val eBookLaunchMailSender: EBookLaunchMailSender,
    private val welcomeSubscriberMailSender: WelcomeSubscriberMailSender,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MailService::class.java)
        private const val LIMIT = 100
        private const val MIN_SUBSCRIBER_COUNT = 10
    }

    fun sendDaily(command: SendStoryDailyEmailCommand) {
        logger.add("story_id", command.storyId)
        logger.add("command", "SendStoryDailyEmailCommand")

        // Story
        val story = storyService.findById(command.storyId)
        val content = storyService.findContent(story, story.language).getOrNull() ?: return
        val blog = userDao.findById(story.userId).get()

        var delivered = 0
        var failed = 0
        var offset = 0
        var blacklisted = 0
        while (true) {
            // Subscribers
            val subscriberIds = subscriptionDao.findByUserId(
                story.userId,
                PageRequest.of(offset / LIMIT, LIMIT)
            ).map { it.subscriberId }
            if (subscriberIds.isEmpty()) {
                break
            }

            // Recipients
            val recipients = userDao.findAllById(subscriberIds)

            // Send
            val otherStories = findOtherStories(story)
            val store = findStore(blog)
            recipients.forEach { recipient ->
                if (recipient.email.isNullOrEmpty()) {
                    // Do nothing
                } else if (xemailService.contains(recipient.email!!)) {
                    blacklisted++
                } else {
                    try {
                        val products = store?.let { findProducts(story, store, recipient) } ?: emptyList()
                        if (dailyMailSender.send(blog, store, content, recipient, otherStories, products)) {
                            delivered++
                        }
                    } catch (ex: Exception) {
                        LOGGER.warn("Unable to send daily email to User#${recipient.id}", ex)
                        failed++
                    }
                }
            }

            // Next
            if (subscriberIds.size < LIMIT) {
                break
            }
            offset += LIMIT
        }
        logger.add("subscriber_count", blog.subscriberCount)
        logger.add("delivery_count", delivered)
        logger.add("blacklist_count", blacklisted)
        logger.add("error_count", failed)
    }

    fun sendWeekly() {
        // Stories of the last week
        val today = LocalDate.now()
        val stories = storyService.searchStories(
            SearchStoryRequest(
                status = StoryStatus.PUBLISHED,
                activeUserOnly = true,
                publishedStartDate = DateUtils.toDate(today.minusDays(8)),
                publishedEndDate = DateUtils.toDate(today.minusDays(1)),
                limit = 200
            )
        )

        // Filter user's having enough subscribers
        val users = userDao.findAllById(
            stories.map { it.userId }.toSet()
        )
            .filter { user -> user.subscriberCount >= MIN_SUBSCRIBER_COUNT }
            .toList()

        // Filter out stories from users not having enough subscribers
        val userIds = users.map { user -> user.id }
        val xstories = stories.filter { story -> userIds.contains(story.userId) }

        var recipientCount = 0
        var deliveryCount = 0
        var errorCount = 0
        var offset = 0
        var blacklistCount = 0
        val products = findProducts()
        while (true) {
            // Recipients
            val recipients = userDao.findBySuspended(false, PageRequest.of(offset / LIMIT, LIMIT))
            if (recipients.isEmpty()) {
                break
            }

            // Send
            recipients.forEach { recipient ->
                recipientCount++

                if (recipient.email.isNullOrEmpty()) {
                    // Do nothing
                } else if (xemailService.contains(recipient.email!!)) {
                    blacklistCount++
                } else {
                    try {
                        if (weeklyMailSender.send(xstories, users, recipient, products)) {
                            deliveryCount++
                        }
                    } catch (ex: Exception) {
                        LOGGER.warn("Unable to send weekly email to User#${recipient.id}", ex)
                        errorCount++
                    }
                }
            }

            // Next
            offset += LIMIT
        }
        logger.add("story_count", stories.size)
        logger.add("recipient_count", recipientCount)
        logger.add("delivery_count", deliveryCount)
        logger.add("blacklist_count", blacklistCount)
        logger.add("error_count", errorCount)
    }

    fun onTransactionSuccessful(tx: TransactionEntity) {
        if (tx.type == TransactionType.CHARGE && tx.status == Status.SUCCESSFUL) {
            try {
                val messageId = orderMailSender.send(tx)
                logger.add("message_id", messageId)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to send transaction receipt to ${tx.email}", ex)
            }
        }
    }

    fun sendAbandonedHourlyEmail(tx: TransactionEntity): String? =
        if (tx.status == Status.FAILED) {
            abandonedMailSender.send(tx, EventType.TRANSACTION_ABANDONED_HOURLY_EMAIL_SENT_EVENT)
        } else {
            null
        }

    fun sendAbandonedDailyEmail(tx: TransactionEntity): String? =
        if (tx.status == Status.FAILED) {
            abandonedMailSender.send(tx, EventType.TRANSACTION_ABANDONED_DAILY_EMAIL_SENT_EVENT)
        } else {
            null
        }

    fun sendAbandonedWeeklyEmail(tx: TransactionEntity): String? =
        if (tx.status == Status.FAILED) {
            abandonedMailSender.send(tx, EventType.TRANSACTION_ABANDONED_WEEKLY_EMAIL_SENT_EVENT)
        } else {
            null
        }

    fun sendEBookLaunch(product: ProductEntity) {
        val author = userDao.findById(product.store.userId).get()
        var delivered = 0
        var failed = 0
        var offset = 0
        while (true) {
            // Subscribers
            val subscriberIds = subscriptionDao.findByUserId(
                author.id!!,
                PageRequest.of(offset / LIMIT, LIMIT)
            ).map { it.subscriberId }
            if (subscriberIds.isEmpty()) {
                break
            }

            // Recipients
            val recipients = userDao.findAllById(subscriberIds)

            // Send
            recipients.forEach { recipient ->
                try {
                    if (eBookLaunchMailSender.send(product, author, recipient)) {
                        delivered++
                    }
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to send daily email to User#${recipient.id}", ex)
                    failed++
                }
            }

            // Next
            if (subscriberIds.size < LIMIT) {
                break
            }
            offset += LIMIT
        }
        logger.add("subscriber_count", author.subscriberCount)
        logger.add("delivery_count", delivered)
        logger.add("error_count", failed)
    }

    fun onSubscribed(blogId: Long, subscriberId: Long) {
        try {
            val blog = userDao.findById(blogId).get()
            val recipient = userDao.findById(subscriberId).get()
            val stories = storyService.searchStories(
                SearchStoryRequest(
                    userIds = listOf(blogId),
                    status = StoryStatus.PUBLISHED,
                    sortBy = StorySortStrategy.PUBLISHED,
                    sortOrder = SortOrder.DESCENDING,
                    limit = 5,
                )
            )
            welcomeSubscriberMailSender.send(blog, recipient, stories)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to send welcome email", ex)
        }
    }

    private fun findOtherStories(story: StoryEntity): List<StoryEntity> =
        try {
            storyService.searchStories(
                SearchStoryRequest(
                    userIds = listOf(story.userId),
                    sortBy = StorySortStrategy.PUBLISHED,
                    status = StoryStatus.PUBLISHED,
                    limit = 10,
                ),
            ).filter { it.id != story.id }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find other stories", ex)
            emptyList()
        }

    private fun findStore(blog: UserEntity): StoreEntity? =
        blog.storeId?.let { storeId -> storeDao.findById(storeId).getOrNull() }

    private fun findProducts(story: StoryEntity, store: StoreEntity, recipient: UserEntity): List<ProductEntity> =
        try {
            productService.searchProducts(
                SearchProductRequest(
                    storeIds = listOf(store.id ?: ""),
                    sortBy = ProductSortStrategy.ORDER_COUNT,
                    sortOrder = SortOrder.DESCENDING,
                    status = ProductStatus.PUBLISHED,
                    searchContext = SearchProductContext(
                        storyId = story.id,
                        userId = recipient.id
                    )
                ),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find products", ex)
            emptyList()
        }

    private fun findProducts(): List<ProductEntity> =
        try {
            productService.searchProducts(
                SearchProductRequest(
                    sortBy = ProductSortStrategy.ORDER_COUNT,
                    sortOrder = SortOrder.DESCENDING,
                    status = ProductStatus.PUBLISHED,
                ),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find products", ex)
            emptyList()
        }
}
