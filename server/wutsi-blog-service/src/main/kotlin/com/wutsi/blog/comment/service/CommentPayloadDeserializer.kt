package com.wutsi.blog.comment.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.comment.dto.StoryCommentedEventPayload
import com.wutsi.blog.event.EventType.STORY_COMMENTED_EVENT
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.event.store.PayloadDeserializer
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class CommentPayloadDeserializer(
    private val root: RootPayloadDeserializer,
    private val objectMapper: ObjectMapper,
) : PayloadDeserializer {
    @PostConstruct
    fun init() {
        root.register(STORY_COMMENTED_EVENT, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            STORY_COMMENTED_EVENT -> objectMapper.readValue(payload, StoryCommentedEventPayload::class.java)
            else -> null
        }
}
