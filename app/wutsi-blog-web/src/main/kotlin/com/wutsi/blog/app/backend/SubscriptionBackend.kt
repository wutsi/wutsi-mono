package com.wutsi.blog.app.backend

import com.wutsi.blog.event.EventType
import com.wutsi.blog.subscription.dto.CountSubscriptionRequest
import com.wutsi.blog.subscription.dto.CountSubscriptionResponse
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.subscription.dto.UnsubscribeCommand
import com.wutsi.platform.core.stream.EventStream
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SubscriptionBackend(
    private val rest: RestTemplate,
    private val eventStream: EventStream,
) {
    @Value("\${wutsi.application.backend.subscription.endpoint}")
    private lateinit var endpoint: String

    fun count(request: CountSubscriptionRequest): CountSubscriptionResponse {
        return rest.postForEntity("$endpoint/queries/count", request, CountSubscriptionResponse::class.java).body!!
    }

    fun execute(command: SubscribeCommand) {
        eventStream.publish(EventType.SUBSCRIBE_COMMAND, command)
    }

    fun execute(command: UnsubscribeCommand) {
        eventStream.publish(EventType.UNSUBSCRIBE_COMMAND, command)
    }
}
