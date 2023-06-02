package com.wutsi.blog.subscription.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.SUBSCRIBED_EVENT
import com.wutsi.blog.event.EventType.SUBSCRIBE_COMMAND
import com.wutsi.blog.event.EventType.UNSUBSCRIBED_EVENT
import com.wutsi.blog.event.EventType.UNSUBSCRIBE_COMMAND
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.subscription.dto.UnsubscribeCommand
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class SubscriptionPayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(SUBSCRIBE_COMMAND, this)
        root.register(UNSUBSCRIBE_COMMAND, this)

        root.register(SUBSCRIBED_EVENT, this)
        root.register(UNSUBSCRIBED_EVENT, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            SUBSCRIBED_EVENT -> objectMapper.readValue(payload, EventPayload::class.java)
            UNSUBSCRIBED_EVENT -> objectMapper.readValue(payload, EventPayload::class.java)

            SUBSCRIBE_COMMAND -> objectMapper.readValue(payload, SubscribeCommand::class.java)
            UNSUBSCRIBE_COMMAND -> objectMapper.readValue(payload, UnsubscribeCommand::class.java)
            else -> null
        }
}
