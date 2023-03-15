package com.wutsi.platform.core.messaging.whatsapp

import com.wutsi.platform.core.messaging.Message

class WAMessagingServiceNone : WAMessagingService {
    override fun send(message: Message): String = ""
}
