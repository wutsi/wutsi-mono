package com.wutsi.blog.app.backend

import com.wutsi.blog.event.EventType
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.subscription.dto.UnsubscribeCommand
import com.wutsi.platform.core.stream.EventStream
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SubscriptionBackend(
    private val eventStream: EventStream,
) {
    @Value("\${wutsi.application.backend.subscription.endpoint}")
    private lateinit var endpoint: String

    fun subscribe(command: SubscribeCommand) {
        eventStream.publish(EventType.SUBSCRIBE_COMMAND, command)
    }

    fun unsubscribe(command: UnsubscribeCommand) {
        eventStream.publish(EventType.UNSUBSCRIBE_COMMAND, command)
    }
}
