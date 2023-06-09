package com.wutsi.blog.like.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_LIKED_EVENT
import com.wutsi.blog.event.EventType.STORY_UNLIKED_EVENT
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class LikePayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(STORY_LIKED_EVENT, this)
        root.register(STORY_UNLIKED_EVENT, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            STORY_LIKED_EVENT -> objectMapper.readValue(payload, EventPayload::class.java)
            STORY_UNLIKED_EVENT -> objectMapper.readValue(payload, EventPayload::class.java)
            else -> null
        }
}
