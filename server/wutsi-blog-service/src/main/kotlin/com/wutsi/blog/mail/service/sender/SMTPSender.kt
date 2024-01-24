package com.wutsi.blog.mail.service.sender

import com.wutsi.blog.mail.service.XEmailService
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class SMTPSender(
    private val messagingServiceProvider: MessagingServiceProvider,
    private val xEmailService: XEmailService,
    @Value("\${wutsi.application.mail.whitelist}") private val whitelist: String,
    @Value("\${wutsi.application.mail.smtp.enabled}") private val enabled: Boolean,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SMTPSender::class.java)
    }

    @PostConstruct
    fun init() {
        LOGGER.info(">>> Email Whitelist: $whitelist")
    }

    fun send(message: Message): String? {
        if (!enabled) {
            LOGGER.warn(">>> SMTP delivery is disabled")
            return null
        }

        val email = message.recipient.email

        // blacklist?
        if (isBlacklisted(email)) {
            LOGGER.warn(">>> email=$email - Blacklisted")
            return null
        }

        // whitelist?
        return if (isWhitelisted(email)) {
            messagingServiceProvider.get(MessagingType.EMAIL).send(message)
        } else {
            LOGGER.warn(">>> email=$email - Not whitelisted")
            null
        }
    }

    private fun isWhitelisted(email: String): Boolean =
        whitelist == "*" || whitelist.contains(email)

    private fun isBlacklisted(email: String): Boolean =
        xEmailService.contains(email)
}
