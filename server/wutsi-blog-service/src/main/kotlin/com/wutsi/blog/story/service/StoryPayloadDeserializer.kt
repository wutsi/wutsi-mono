package com.wutsi.blog.story.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType.STORY_IMPORT_FAILED_EVENT
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.blog.story.dto.StoryImportFailedEventPayload
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class StoryPayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(STORY_IMPORT_FAILED_EVENT, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            STORY_IMPORT_FAILED_EVENT -> objectMapper.readValue(payload, StoryImportFailedEventPayload::class.java)
            else -> null
        }
}
