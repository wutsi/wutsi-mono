package com.wutsi.blog.mail.service

import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.dao.XEmailRepository
import com.wutsi.blog.mail.domain.XEmailEntity
import com.wutsi.blog.mail.dto.EmailBouncedEvent
import com.wutsi.blog.mail.dto.EmailComplainedEvent
import com.wutsi.blog.mail.dto.NotificationType
import com.wutsi.blog.mail.service.ses.SESNotification
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody

@Service
class XEmailService(
    private val logger: KVLogger,
    private val dao: XEmailRepository,
    private val eventStore: EventStore,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(XEmailService::class.java)
    }

    fun contains(email: String): Boolean {
        val id = toId(email)
        return dao.findById(id).isPresent
    }

    @Transactional
    fun process(@RequestBody request: SESNotification): Boolean {
        logger.add("bounce_type", request.bounce?.bounceType)

        var count = 0
        when (request.notificationType?.lowercase()) {
            "bounce" -> if (request.bounce?.bounceType?.lowercase() == "permanent") {
                request.bounce.bouncedRecipients.forEach { recipient ->
                    if (onBounced(
                            EmailBouncedEvent(
                                email = recipient.emailAddress,
                                messageId = request.mail.messageId,
                            )
                        )
                    ) {
                        count++
                    }
                }
            }

            "complaint" -> request.complaint?.complainedRecipients?.forEach { recipient ->
                if (onComplained(
                        EmailComplainedEvent(
                            email = recipient.emailAddress,
                            messageId = request.mail.messageId,
                        )
                    )
                ) {
                    count++
                }
            }

            else -> {
                LOGGER.info(">>> ${request.notificationType} - ignored")
            }
        }
        return count > 0
    }

    @Transactional
    fun onComplained(payload: EmailComplainedEvent): Boolean {
        logger.add("payload_email", payload.email)
        logger.add("payload_message_id", payload.messageId)
        if (add(payload.email, NotificationType.COMPLAIN)) {
            notify(EventType.EMAIL_COMPLAINED_EVENT, payload.email)
            return true
        }
        return false
    }

    @Transactional
    fun onBounced(payload: EmailBouncedEvent): Boolean {
        logger.add("payload_email", payload.email)
        logger.add("payload_message_id", payload.messageId)
        if (add(payload.email, NotificationType.BOUNCE)) {
            notify(EventType.EMAIL_BOUNCED_EVENT, payload.email)
            return true
        }
        return false
    }

    private fun add(email: String, type: NotificationType): Boolean {
        if (contains(email)) {
            LOGGER.warn(">>> $email is already blacklisted")
            return false
        }

        LOGGER.warn(">>> Blacklisting $email")
        dao.save(
            XEmailEntity(
                id = toId(email),
                email = normalizeEmail(email),
                type = type,
            )
        )
        return true
    }

    private fun normalizeEmail(email: String) = email.lowercase().trim()
    private fun toId(email: String) = DigestUtils.md5Hex(normalizeEmail(email)).lowercase()

    private fun notify(type: String, email: String) {
        eventStore.store(
            Event(
                streamId = StreamId.EMAIL_NOTIFICATION,
                type = type,
                entityId = DigestUtils.md5Hex(email),
                metadata = mapOf(
                    "email" to email
                )
            ),
        )
    }
}
