package com.wutsi.blog.mail.endpoint

import com.wutsi.blog.event.EventType
import com.wutsi.blog.mail.dto.EmailBouncedEvent
import com.wutsi.blog.mail.service.ses.SESNotification
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayOutputStream

@RestController
@RequestMapping("/webhooks/ses")
class SESWebhook(
    private val stream: EventStream,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SESWebhook::class.java)
    }

    @PostMapping(consumes = ["text/plain;charset=UTF-8"], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun configuration(request: HttpServletRequest) {
        val out = ByteArrayOutputStream()
        IOUtils.copy(request.inputStream, out)
        LOGGER.info(">>> Confirmation Message{\n$out")
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun notify(@RequestBody request: SESNotification) {
        logger.add("request_notification_type", request.notificationType)
        logger.add("request_bounce_type", request.bounce?.bounceType)
        logger.add("request_bounce_recipients", request.bounce?.bouncedRecipients?.map { it.emailAddress })
        logger.add("request_complaint_feedback_type", request.complaint?.complaintFeedbackType)
        logger.add("request_complaint_recipients", request.complaint?.complainedRecipients?.map { it.emailAddress })

        when (request.notificationType?.lowercase()) {
            "bounce" -> if (request.bounce?.bounceType?.lowercase() == "permanent") {
                request.bounce?.bouncedRecipients?.forEach { recipient ->
                    stream.enqueue(
                        EventType.EMAIL_BOUNCED_EVENT,
                        EmailBouncedEvent(
                            email = recipient.emailAddress,
                            messageId = request.mail.messageId,
                        )
                    )
                }
            }

            "complaint" -> request.complaint?.complainedRecipients?.forEach { recipient ->
                stream.enqueue(
                    EventType.EMAIL_COMPLAINED_EVENT,
                    EmailBouncedEvent(
                        email = recipient.emailAddress,
                        messageId = request.mail.messageId,
                    )
                )
            }

            else -> {}
        }
    }
}
