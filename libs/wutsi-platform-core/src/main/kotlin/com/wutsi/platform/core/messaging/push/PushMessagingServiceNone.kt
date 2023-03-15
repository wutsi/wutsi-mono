package com.wutsi.platform.core.messaging.push

import com.wutsi.platform.core.messaging.Message

class PushMessagingServiceNone : PushMessagingService {
    override fun send(message: Message) = ""
}
