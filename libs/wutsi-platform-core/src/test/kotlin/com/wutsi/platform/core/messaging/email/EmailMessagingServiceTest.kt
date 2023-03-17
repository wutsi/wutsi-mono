package com.wutsi.platform.core.messaging.email

import com.amazonaws.util.IOUtils
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingService
import com.wutsi.platform.core.messaging.Party
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mail.javamail.JavaMailSender
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

internal class EmailMessagingServiceTest {
    private val from = "ray.sponsible@gmail.com"
    private lateinit var mailer: JavaMailSender
    private lateinit var service: MessagingService

    @BeforeEach
    fun setUp() {
        mailer = mock()
        doReturn(MimeMessage(mock<MimeMessage>())).whenever(mailer).createMimeMessage()
        service = EmailMessagingService(mailer, from)
    }

    @Test
    fun send() {
        // GIVEN
        val request = createMessage()

        // WHEN
        val messageId = service.send(request)

        // THEN
        assertNotNull(messageId)

        val message = argumentCaptor<MimeMessage>()
        verify(mailer).send(message.capture())

        assertEquals(InternetAddress(from, "Wutsi"), message.firstValue.from[0])
        assertEquals(request.subject, message.firstValue.subject)
        assertEquals(
            InternetAddress(request.recipient.email, request.recipient.displayName),
            message.firstValue.allRecipients[0],
        )
        assertTrue(message.firstValue.contentType.contains(request.mimeType))

        val body = IOUtils.toString(message.firstValue.inputStream)
        assertTrue(body.contains(request.body))
    }

    private fun createMessage(sender: Party? = null) = Message(
        sender = sender,
        recipient = Party("ray.sponsible@gmail.com", displayName = "Ray Sponsible"),
        subject = "Hello world",
        language = "en",
        mimeType = "text/plain",
        body = "Yo man",
    )
}
