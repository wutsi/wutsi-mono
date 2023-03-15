package com.wutsi.platform.core.messaging.sms

import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.Party
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SMSMessagingServiceNoneTest {
    @Test
    fun send() {
        val id = SMSMessagingServiceNone().send(Message(recipient = Party()))

        assertEquals(36, id.length)
    }
}
