package com.wutsi.platform.core.messaging.email

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingService
import com.wutsi.platform.core.messaging.Party
import jakarta.mail.Session
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mail.javamail.JavaMailSender
import java.util.Properties

internal class EmailMessagingServiceTest {
    private val from = "ray.sponsible@gmail.com"
    private lateinit var mailer: JavaMailSender
    private lateinit var service: MessagingService

    @BeforeEach
    fun setUp() {
        mailer = mock()
        val session = Session.getInstance(Properties())
        doReturn(MimeMessage(session)).whenever(mailer).createMimeMessage()
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
        assertNull(message.firstValue.getRecipients(jakarta.mail.Message.RecipientType.BCC))
        assertTrue(message.firstValue.contentType.contains(request.mimeType))

        request.headers.forEach {
            assertEquals(it.value, message.firstValue.getHeader(it.key)[0])
        }

        val body = IOUtils.toString(message.firstValue.inputStream)
        assertTrue(body.contains(request.body))
    }

    @Test
    fun sendWithSender() {
        // GIVEN
        val request = createMessage(
            sender = Party(
                email = "ray.sponsible@gmail.com",
                displayName = "Ray Sponsible",
            ),
        )

        // WHEN
        service.send(request)

        // THEN
        val message = argumentCaptor<MimeMessage>()
        verify(mailer).send(message.capture())

        assertEquals(InternetAddress(from, "Ray Sponsible"), message.firstValue.from[0])
        assertNull(message.firstValue.getRecipients(jakarta.mail.Message.RecipientType.BCC))
    }

    @Test
    fun sendWithBCC() {
        // GIVEN
        val request = createMessage(
            bcc = Party(
                email = "roger.milla@gmail.com",
            ),
        )

        // WHEN
        service.send(request)

        // THEN
        val message = argumentCaptor<MimeMessage>()
        verify(mailer).send(message.capture())

        val bcc = message.firstValue.getRecipients(jakarta.mail.Message.RecipientType.BCC)
        assertEquals(InternetAddress(request.bcc?.email), bcc[0])
    }

    @Test
    fun sendToRecipientWithEmptyDisplayName() {
        // GIVEN
        val request = createMessage(
            recipient = Party(
                email = "ray.sponsible@gmail.com",
                displayName = "",
            ),
        )

        // WHEN
        service.send(request)

        // THEN
        val message = argumentCaptor<MimeMessage>()
        verify(mailer).send(message.capture())

        assertEquals(
            InternetAddress(request.recipient.email),
            message.firstValue.allRecipients[0],
        )
    }

    @Test
    fun sendToRecipientWithNullDisplayName() {
        // GIVEN
        val request = createMessage(
            recipient = Party(
                email = "ray.sponsible@gmail.com",
                displayName = null,
            ),
        )

        // WHEN
        service.send(request)

        // THEN
        val message = argumentCaptor<MimeMessage>()
        verify(mailer).send(message.capture())

        assertEquals(
            InternetAddress(request.recipient.email),
            message.firstValue.allRecipients[0],
        )
    }

    private fun createMessage(
        sender: Party? = null,
        recipient: Party = Party("ray.sponsible@gmail.com", displayName = "Ray Sponsible"),
        bcc: Party? = null,
    ) = Message(
        sender = sender,
        recipient = recipient,
        subject = "Hello world",
        language = "en",
        mimeType = "text/plain",
        body = "Yo man",
        bcc = bcc,
        headers = mapOf(
            "foo" to "bar",
            "yo" to "man",
        )
    )
}
