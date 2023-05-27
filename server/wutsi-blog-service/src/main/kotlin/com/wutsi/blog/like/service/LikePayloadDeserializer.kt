package com.wutsi.blog.like.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.LIKE_STORY_COMMAND
import com.wutsi.blog.event.EventType.STORY_LIKED_EVENT
import com.wutsi.blog.event.EventType.STORY_UNLIKED_EVENT
import com.wutsi.blog.event.EventType.UNLIKE_STORY_COMMAND
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.UnlikeStoryCommand
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

        root.register(LIKE_STORY_COMMAND, this)
        root.register(UNLIKE_STORY_COMMAND, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            STORY_LIKED_EVENT -> objectMapper.readValue(payload, EventPayload::class.java)
            STORY_UNLIKED_EVENT -> objectMapper.readValue(payload, EventPayload::class.java)

            LIKE_STORY_COMMAND -> objectMapper.readValue(payload, LikeStoryCommand::class.java)
            UNLIKE_STORY_COMMAND -> objectMapper.readValue(payload, UnlikeStoryCommand::class.java)
            else -> null
        }
}
