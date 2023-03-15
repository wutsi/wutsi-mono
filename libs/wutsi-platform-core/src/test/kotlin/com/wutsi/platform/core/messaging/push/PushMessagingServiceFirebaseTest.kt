package com.wutsi.platform.core.messaging.push

import com.google.firebase.messaging.FirebaseMessaging
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingService
import com.wutsi.platform.core.messaging.Party
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class PushMessagingServiceFirebaseTest {
    private lateinit var fm: FirebaseMessaging
    private lateinit var service: MessagingService

    @BeforeEach
    fun setUp() {
        fm = mock()
        service = PushMessagingServiceFirebase(fm)
    }

    @Test
    fun send() {
        val id = "1111"
        doReturn(id).whenever(fm).send(any())

        val msg = Message(
            recipient = Party(deviceToken = "43094093403"),
            subject = "Hello world",
            body = "This is a message",
            imageUrl = "https://img.com/1.png",
            data = mapOf(
                "x" to "y",
            ),
        )
        val result = service.send(msg)

        val xmsg = argumentCaptor<com.google.firebase.messaging.Message>()
        verify(fm).send(xmsg.capture())

        assertEquals(id, result)
    }
}
