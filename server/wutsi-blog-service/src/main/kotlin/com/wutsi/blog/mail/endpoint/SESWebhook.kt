package com.wutsi.blog.mail.endpoint

import com.wutsi.blog.email.dto.EmailBouncedEvent
import com.wutsi.blog.event.EventType
import com.wutsi.blog.mail.service.ses.SESNotification
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhooks/ses")
class SESWebhook(
    private val stream: EventStream,
    private val logger: KVLogger,
) {
    @PostMapping
    fun notify(@RequestBody request: SESNotification) {
        logger.add("request_notification_type", request.notificationType)
        logger.add("request_bounce_type", request.bounce?.bounceType)
        logger.add("request_bounce_recipients", request.bounce?.bouncedRecipients?.map { it.emailAddress })
        logger.add("request_complaint_feedback_type", request.complaint?.complaintFeedbackType)
        logger.add("request_complaint_recipients", request.complaint?.complainedRecipients?.map { it.emailAddress })

        when (request.notificationType.lowercase()) {
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
