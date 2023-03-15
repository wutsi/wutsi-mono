package com.wutsi.platform.core.messaging

interface MessagingService {
    fun send(message: Message): String
}
