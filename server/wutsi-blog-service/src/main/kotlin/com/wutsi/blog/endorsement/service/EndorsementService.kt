package com.wutsi.blog.enforsement.service

import com.wutsi.blog.endorsement.dto.EndorseUserCommand
import com.wutsi.blog.enforsement.dao.EndorsementRepository
import com.wutsi.blog.enforsement.domain.EndorsementEntity
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.user.service.UserService
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class EndorsementService(
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val endorsementDao: EndorsementRepository,
    private val userService: UserService,
    private val logger: KVLogger,
    private val em: EntityManager,
) {
    @Transactional
    fun endorse(command: EndorseUserCommand) {
        logger.add("request_user_id", command.userId)
        logger.add("request_endorser_id", command.endorserId)
        logger.add("request_timestamp", command.timestamp)
        logger.add("command", "EndorseCommand")

        if (execute(command)) {
            notify(
                EventType.USER_ENDORSED_EVENT,
                command.userId,
                command.endorserId,
                command.timestamp,
            )
        }
    }

    private fun execute(command: EndorseUserCommand): Boolean {
        // Validation
        if (command.userId == command.endorserId) {
            logger.add("validation_failure", "self_endorsement")
            return false
        }

        // Endorse
        val endorsement = endorsementDao.findByUserIdAndEndorserId(command.userId, command.endorserId)
        if (endorsement == null) {
            val subscription = EndorsementEntity(
                userId = command.userId,
                endorserId = command.endorserId,
                creationDateTime = Date(command.timestamp),
                blurb = command.blurb,
            )
            endorsementDao.save(subscription)
            logger.add("subscription_status", "created")
            return true
        } else {
            return false
        }
    }

    private fun notify(type: String, userId: Long, endorserId: Long?, timestamp: Long) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.ENDORSEMENT,
                type = type,
                entityId = userId.toString(),
                userId = endorserId?.toString(),
                timestamp = Date(timestamp),
            ),
        )
        logger.add("event_id", eventId)

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(type, payload)
        eventStream.publish(type, payload)
    }
}
