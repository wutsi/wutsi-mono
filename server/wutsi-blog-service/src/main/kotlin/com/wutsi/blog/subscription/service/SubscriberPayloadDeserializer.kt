package com.wutsi.blog.subscription.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType.SUBSCRIBED_EVENT
import com.wutsi.blog.event.EventType.SUBSCRIBER_IMPORTED_EVENT
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.blog.subscription.dto.SubscribedEventPayload
import com.wutsi.blog.subscription.dto.SubscriberImportedEventPayload
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class SubscriberPayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(SUBSCRIBER_IMPORTED_EVENT, this)
        root.register(SUBSCRIBED_EVENT, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            SUBSCRIBER_IMPORTED_EVENT -> objectMapper.readValue(payload, SubscriberImportedEventPayload::class.java)
            SUBSCRIBED_EVENT -> objectMapper.readValue(payload, SubscribedEventPayload::class.java)
            else -> null
        }
}
