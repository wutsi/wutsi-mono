package com.wutsi.blog.app.backend

import com.wutsi.blog.event.EventType
import com.wutsi.blog.subscription.dto.ImportSubscriberCommand
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.subscription.dto.SearchSubscriptionResponse
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.subscription.dto.UnsubscribeCommand
import com.wutsi.platform.core.stream.EventStream
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SubscriptionBackend(
    private val eventStream: EventStream,
    private val rest: RestTemplate,
) {
    @Value("\${wutsi.application.backend.subscription.endpoint}")
    private lateinit var endpoint: String

    fun import(command: ImportSubscriberCommand) {
        eventStream.publish(EventType.IMPORT_SUBSCRIBER_COMMAND, command)
    }

    fun subscribe(command: SubscribeCommand) {
        eventStream.publish(EventType.SUBSCRIBE_COMMAND, command)
    }

    fun unsubscribe(command: UnsubscribeCommand) {
        eventStream.publish(EventType.UNSUBSCRIBE_COMMAND, command)
    }

    fun search(request: SearchSubscriptionRequest): SearchSubscriptionResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchSubscriptionResponse::class.java).body!!
}
