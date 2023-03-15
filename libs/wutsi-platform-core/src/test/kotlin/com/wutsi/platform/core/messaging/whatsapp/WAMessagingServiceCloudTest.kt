package com.wutsi.platform.core.messaging.whatsapp

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class WAMessagingServiceCloudTest {
    private lateinit var client: WAClient
    private lateinit var service: MessagingService
    private val response = WAResponse(
        messages = listOf(WAMessageID(id = "123")),
    )

    @BeforeEach
    fun setUp() {
        client = mock()
        doReturn(response).whenever(client).messages(any())

        service = WAMessagingServiceCloud(client)
    }

    @Test
    public fun success() {
        // GIVEN
        val request = createMessage()

        // WHEN
        val messageId = service.send(request)

        // THEN
        assertNotNull(messageId)

        val message = argumentCaptor<WAMessage>()
        verify(client).messages(message.capture())

        assertEquals("whatsapp", message.firstValue.messaging_product)
        assertEquals(true, message.firstValue.text.preview_url)
        assertEquals(request.body, message.firstValue.text.body)
        assertEquals("text", message.firstValue.type)
        assertEquals("individual", message.firstValue.recipient_type)
    }

    @Test
    fun deserialize() {
        val json =
            "{'messaging_product':'whatsapp','contacts':[{'input':'15147580191','wa_id':'15147580191'}],'messages':[{'id':'wamid.HBgLMTUxNDc1ODAxOTEVAgARGBI4MzJDRUM4NkRBMTY0QUYzNzQA'}]}"
                .replace('\'', '"')
        val response = ObjectMapper().readValue(json, WAResponse::class.java)
        println(response)
    }

    private fun createMessage(sender: Party? = null) = Message(
        sender = sender,
        recipient = Party("ray.sponsible@gmail.com", "+5147580000"),
        subject = "Hello world",
        language = "en",
        mimeType = "text/plain",
        body = "Yo man",
    )
}
