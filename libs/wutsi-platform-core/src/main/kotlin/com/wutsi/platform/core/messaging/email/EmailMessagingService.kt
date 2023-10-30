package com.wutsi.platform.core.messaging.email

import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingService
import com.wutsi.platform.core.messaging.Party
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import java.util.UUID

class EmailMessagingService(
    private val mail: JavaMailSender,
    private val from: String,
) : MessagingService {
    override fun send(message: Message): String {
        val msg = createMessage(message)
        mail.send(msg)
        return UUID.randomUUID().toString()
    }

    private fun createMessage(message: Message): MimeMessage {
        val senderName = message.sender?.displayName?.ifEmpty { null } ?: "Wutsi"
        val fromAddress = InternetAddress(from, senderName)
        val mime = mail.createMimeMessage()
        mime.addRecipients(jakarta.mail.Message.RecipientType.TO, arrayOf(toAddress(message.recipient)))
        mime.setFrom(fromAddress)
        mime.subject = message.subject
        mime.setContent(message.body, message.mimeType)
        if (message.language != null) {
            mime.contentLanguage = arrayOf(message.language)
        }
        message.headers.forEach {
            mime.addHeader(it.key, it.value)
        }
        return mime
    }

    private fun toAddress(party: Party?): InternetAddress? =
        if (party == null) {
            null
        } else if (party.displayName.isNullOrEmpty()) {
            InternetAddress(party.email)
        } else {
            InternetAddress(party.email, party.displayName)
        }
}
