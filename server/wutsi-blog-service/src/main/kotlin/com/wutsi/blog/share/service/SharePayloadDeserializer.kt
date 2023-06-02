package com.wutsi.blog.share.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_SHARED_EVENT
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class SharePayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(STORY_SHARED_EVENT, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            STORY_SHARED_EVENT -> objectMapper.readValue(payload, EventPayload::class.java)
            else -> null
        }
}
