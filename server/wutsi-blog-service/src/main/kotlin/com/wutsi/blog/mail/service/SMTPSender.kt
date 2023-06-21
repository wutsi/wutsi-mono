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
        val email = message.recipient.email
        return if (isWhitelisted(email)) {
            messagingServiceProvider.get(MessagingType.EMAIL).send(message)
        } else {
            LOGGER.warn(">>> $email is not whitelisted")
            null
        }
    }

    private fun isWhitelisted(email: String): Boolean =
        whitelist == "*" || whitelist.contains(email)
}
