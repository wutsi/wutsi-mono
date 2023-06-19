package com.wutsi.blog.mail.service

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
    @Value("\${wutsi.application.mail.debug}") private val debug: String,
    @Value("\${wutsi.application.mail.whitelist}") private val whitelist: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SMTPSender::class.java)
    }

    @PostConstruct
    fun init() {
        LOGGER.info(">>> Email Whitelist: $whitelist")
    }

    fun send(message: Message): String? {
        debug(message)

        return if (isWhitelisted(message.recipient.email)) {
            messagingServiceProvider.get(MessagingType.EMAIL).send(message)
        } else {
            null
        }
    }

    private fun isWhitelisted(email: String): Boolean =
        if (whitelist == "*" || whitelist.contains(email)) {
            true
        } else {
            LOGGER.warn("$email is not whitelisted")
            false
        }

    private fun debug(message: Message) {
        if (!debug.toBoolean()) {
            return
        }
        LOGGER.info("Mime-Type: ${message.mimeType}>")
        LOGGER.info("To: ${message.recipient.displayName}< ${message.recipient.email}>")
        LOGGER.info("Subject: ${message.subject}>")
        LOGGER.info("${message.body}\n")
    }
}
