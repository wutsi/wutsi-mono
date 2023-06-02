package com.wutsi.blog.user.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.USER_ATTRIBUTE_UPDATED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.user.dao.UserEntityRepository
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.dto.UpdateUserAttributeCommand
import com.wutsi.blog.user.dto.UserAttributeUpdatedEvent
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

@Service
class UserService(
    private val dao: UserEntityRepository,
    private val clock: Clock,
    private val logger: KVLogger,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
) {
    fun findById(id: Long): UserEntity {
        val user = dao
            .findById(id)
            .orElseThrow { NotFoundException(Error("user_not_found")) }

        if (user.suspended) {
            throw NotFoundException(Error("session_expired"))
        }

        return user
    }

    @Transactional
    fun updateAttribute(command: UpdateUserAttributeCommand) {
        logger.add("command_user_id", command.userId)
        logger.add("command_name", command.name)
        logger.add("command_value", command.value)

        set(command.userId, command.name, command.value)

        val eventId = eventStore.store(
            Event(
                streamId = StreamId.USER,
                type = USER_ATTRIBUTE_UPDATED_EVENT,
                entityId = command.userId.toString(),
                payload = UserAttributeUpdatedEvent(
                    name = command.name,
                    value = command.value,
                ),
            ),
        )
        logger.add("evt_id", eventId)

        eventStream.publish(USER_ATTRIBUTE_UPDATED_EVENT, EventPayload(eventId = eventId))
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
}
