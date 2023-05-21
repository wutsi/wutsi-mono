package com.wutsi.blog.like.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.like.dto.LikeEventType.LIKE_STORY
import com.wutsi.blog.like.dto.LikeEventType.STORY_LIKED
import com.wutsi.blog.like.dto.LikeEventType.STORY_UNLIKED
import com.wutsi.blog.like.dto.LikeEventType.UNLIKE_STORY
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.StoryLikedEvent
import com.wutsi.blog.like.dto.StoryUnlikedEvent
import com.wutsi.blog.like.dto.UnlikeStoryCommand
import com.wutsi.platform.core.stream.Event
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class LikeEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val service: LikeService,
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(STORY_LIKED, this)
        root.register(STORY_UNLIKED, this)
        root.register(LIKE_STORY, this)
        root.register(UNLIKE_STORY, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            STORY_LIKED -> service.onLiked(objectMapper.readValue(event.payload, StoryLikedEvent::class.java))
            STORY_UNLIKED -> service.onUnliked(objectMapper.readValue(event.payload, StoryUnlikedEvent::class.java))
            LIKE_STORY -> service.like(objectMapper.readValue(event.payload, LikeStoryCommand::class.java))
            UNLIKE_STORY -> service.unlike(objectMapper.readValue(event.payload, UnlikeStoryCommand::class.java))
            else -> {}
        }
    }
}
