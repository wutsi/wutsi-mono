package com.wutsi.platform.core.messaging.sms

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sns.model.PublishResult
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.Party
import com.wutsi.platform.core.messaging.UrlShortener
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SMSMessagingServiceAWSTest {
    private lateinit var amazonSNS: AmazonSNS
    private lateinit var service: SMSMessagingServiceAWS
    private lateinit var urlShortener: UrlShortener

    @BeforeEach
    fun setUp() {
        amazonSNS = mock()
        urlShortener = mock()
        service = SMSMessagingServiceAWS(amazonSNS, urlShortener)
    }

    @Test
    fun `send message`() {
        doReturn(PublishResult().withMessageId("111")).whenever(amazonSNS).publish(any())

        val messageId = service.send(createMessage("+23774511100", "Yo man"))

        val request = argumentCaptor<PublishRequest>()
        verify(amazonSNS).publish(request.capture())

        assertEquals("111", messageId)
        assertEquals("+23774511100", request.firstValue.phoneNumber)
        assertEquals("Yo man", request.firstValue.message)
    }

    @Test
    fun `send message with accent`() {
        doReturn(PublishResult().withMessageId("111")).whenever(amazonSNS).publish(any())

        val messageId = service.send(createMessage("+23774511100", "Wutsi: Vous avez reçu un paiement de Hervé"))

        val request = argumentCaptor<PublishRequest>()
        verify(amazonSNS).publish(request.capture())

        assertEquals("111", messageId)
        assertEquals("+23774511100", request.firstValue.phoneNumber)
        assertEquals("Wutsi: Vous avez recu un paiement de Herve", request.firstValue.message)
    }

    @Test
    fun `send message with url`() {
        doReturn("https://bit.ly/1243").whenever(urlShortener).shorten(any())

        doReturn(PublishResult().withMessageId("111")).whenever(amazonSNS).publish(any())

        val messageId = service.send(
            createMessage(
                "+23774511100",
                "Wutsi: Hello",
                "https://www.google.ca",
            ),
        )

        val request = argumentCaptor<PublishRequest>()
        verify(amazonSNS).publish(request.capture())

        assertEquals("111", messageId)
        assertEquals("+23774511100", request.firstValue.phoneNumber)
        assertEquals("Wutsi: Hello https://bit.ly/1243", request.firstValue.message)
    }

    private fun createMessage(phoneNumber: String, body: String, url: String? = null) = Message(
        recipient = Party(phoneNumber = phoneNumber),
        body = body,
        url = url,
    )
}
