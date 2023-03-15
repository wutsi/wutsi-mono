package com.wutsi.platform.core.messaging.sms

import com.wutsi.platform.core.messaging.Message
import java.util.UUID

class SMSMessagingServiceNone : SMSMessagingService {
    override fun send(message: Message): String =
        UUID.randomUUID().toString()
}
