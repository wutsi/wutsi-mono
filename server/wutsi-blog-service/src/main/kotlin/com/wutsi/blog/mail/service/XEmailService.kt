package com.wutsi.blog.mail.service

import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.dao.XEmailRepository
import com.wutsi.blog.mail.domain.XEmailEntity
import com.wutsi.blog.mail.dto.EmailBouncedEvent
import com.wutsi.blog.mail.dto.EmailComplainedEvent
import com.wutsi.blog.mail.dto.NotificationType
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class XEmailService(
    private val logger: KVLogger,
    private val dao: XEmailRepository,
    private val eventStore: EventStore,
) {
    fun contains(email: String): Boolean {
        val id = toId(email)
        return dao.findById(id).isPresent
    }

    @Transactional
    fun onComplained(payload: EmailComplainedEvent) {
        logger.add("payload_email", payload.email)
        logger.add("payload_message_id", payload.messageId)
        if (add(payload.email, NotificationType.COMPLAIN)) {
            notify(EventType.EMAIL_COMPLAINED_EVENT, payload.email)
        }
    }

    @Transactional
    fun onBounced(payload: EmailBouncedEvent) {
        logger.add("payload_email", payload.email)
        logger.add("payload_message_id", payload.messageId)
        if (add(payload.email, NotificationType.BOUNCE)) {
            notify(EventType.EMAIL_BOUNCED_EVENT, payload.email)
        }
    }

    private fun add(email: String, type: NotificationType): Boolean {
        val id = toId(email)
        val entity = dao.findById(id).getOrNull()
        if (entity != null) {
            return false
        }

        dao.save(
            XEmailEntity(
                id = id,
                email = normalizeEmail(email),
                type = type,
            )
        )
        return true
    }

    private fun normalizeEmail(email: String) = email.lowercase()
    private fun toId(email: String) = DigestUtils.md5Hex(normalizeEmail(email)).lowercase()

    private fun notify(type: String, email: String) {
        eventStore.store(
            Event(
                streamId = StreamId.EMAIL_NOTIFICATION,
                type = type,
                entityId = email,
            ),
        )
    }
}
