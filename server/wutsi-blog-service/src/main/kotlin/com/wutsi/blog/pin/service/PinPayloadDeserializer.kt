package com.wutsi.blog.pin.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_PINED_EVENT
import com.wutsi.blog.event.EventType.STORY_UNPINED_EVENT
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class PinPayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(STORY_PINED_EVENT, this)
        root.register(STORY_UNPINED_EVENT, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            STORY_PINED_EVENT -> objectMapper.readValue(payload, EventPayload::class.java)
            STORY_UNPINED_EVENT -> objectMapper.readValue(payload, EventPayload::class.java)
            else -> null
        }
}
