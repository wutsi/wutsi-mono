package com.wutsi.blog.like.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.RootPayloadDeserializer
import com.wutsi.blog.like.dto.LikeEventType
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.StoryLikedEvent
import com.wutsi.blog.like.dto.StoryUnlikedEvent
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
        root.register(LikeEventType.STORY_LIKED, this)
        root.register(LikeEventType.STORY_UNLIKED, this)

        root.register(LikeEventType.LIKE_STORY, this)
        root.register(LikeEventType.UNLIKE_STORY, this)
    }

    override fun deserialize(type: String, payload: String): Any? =
        when (type) {
            LikeEventType.STORY_LIKED -> objectMapper.readValue(payload, StoryLikedEvent::class.java)
            LikeEventType.STORY_UNLIKED -> objectMapper.readValue(payload, StoryUnlikedEvent::class.java)

            LikeEventType.LIKE_STORY -> objectMapper.readValue(payload, LikeStoryCommand::class.java)
            LikeEventType.UNLIKE_STORY -> objectMapper.readValue(payload, UnlikeStoryCommand::class.java)
            else -> null
        }
}
