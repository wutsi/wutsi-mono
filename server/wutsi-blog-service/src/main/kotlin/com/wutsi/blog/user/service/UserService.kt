package com.wutsi.blog.user.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.EventType.BLOG_CREATED_EVENT
import com.wutsi.blog.event.EventType.USER_ATTRIBUTE_UPDATED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.user.dao.SearchUserQueryBuilder
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.dto.CreateBlogCommand
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
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.util.Date
import javax.persistence.EntityManager

@Service
class UserService(
    private val dao: UserRepository,
    private val storyDao: StoryRepository,
    private val clock: Clock,
    private val logger: KVLogger,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val em: EntityManager,
) {
    fun findById(id: Long): UserEntity {
        val user = dao.findById(id)
            .orElseThrow { NotFoundException(Error("user_not_found")) }

        if (user.suspended) {
            throw NotFoundException(Error("user_suspended"))
        }

        return user
    }

    fun findByIds(ids: List<Long>): List<UserEntity> =
        dao.findAllById(ids).filter { !it.suspended }

    fun findByName(name: String): UserEntity {
        val user = dao.findByNameIgnoreCase(name.lowercase())
            .orElseThrow { NotFoundException(Error("user_not_found")) }

        if (user.suspended) {
            throw NotFoundException(Error("session_expired"))
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
        dao.save(user)
    }

    @Transactional
    fun onKpisImported(user: UserEntity) {
        user.readCount = storyDao.sumReadCountByUserId(user.id!!)
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

    private fun updateSubscriberCount(user: UserEntity) {
        user.subscriberCount =
            count(StreamId.SUBSCRIPTION, user, EventType.SUBSCRIBED_EVENT) - count(
                StreamId.SUBSCRIPTION,
                user,
                EventType.UNSUBSCRIBED_EVENT,
            )
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
        val user = findById(command.userId)
        if (!user.blog) {
            set(command.userId, "blog", "true")
            notify(BLOG_CREATED_EVENT, command.userId)
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

    private fun set(id: Long, name: String, value: String?): UserEntity {
        val user = findById(id)
        val name = name.lowercase()

        if ("name" == name) {
            rename(user, value!!)
        } else if ("email" == name) {
            updateEmail(user, value!!)
        } else if ("full_name" == name) {
            user.fullName = value ?: ""
        } else if ("biography" == name) {
            user.biography = value
        } else if ("picture_url" == name) {
            user.pictureUrl = value
        } else if ("website_url" == name) {
            user.websiteUrl = value
        } else if ("language" == name) {
            user.language = value
        } else if ("read_all_languages" == name) {
            user.readAllLanguages = ("true" == value!!)
        } else if ("facebook_id" == name) {
            user.facebookId = value
        } else if ("twitter_id" == name) {
            user.twitterId = value
        } else if ("linkedin_id" == name) {
            user.linkedinId = value
        } else if ("youtube_id" == name) {
            user.youtubeId = value
        } else if ("blog" == name) {
            user.blog = value?.toBoolean() == true
        } else if ("whatsapp_id" == name) {
            user.whatsappId = value
        } else if ("telegram_id" == name) {
            user.telegramId = value
        } else {
            throw ConflictException(Error("invalid_attribute"))
        }

        user.lastLoginDateTime = Date(clock.millis())
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
            throw ConflictException(Error("duplicate_name"))
        }
    }

    private fun checkEmailUnique(user: UserEntity, name: String) {
        val dup = dao.findByEmailIgnoreCase(name)
        if (dup.isPresent && dup.get().id != user.id) {
            throw ConflictException(Error("duplicate_email"))
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

        eventStream.publish(type, EventPayload(eventId = eventId))
    }
}
