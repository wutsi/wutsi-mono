package com.wutsi.blog.like.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.like.dto.LikeEventType.LIKE_STORY_COMMAND
import com.wutsi.blog.like.dto.LikeEventType.STORY_LIKED
import com.wutsi.blog.like.dto.LikeEventType.STORY_UNLIKED
import com.wutsi.blog.like.dto.LikeEventType.UNLIKE_STORY_COMMAND
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.StoryLikedEvent
import com.wutsi.blog.like.dto.StoryUnlikedEvent
import com.wutsi.blog.like.dto.UnlikeStoryCommand
import com.wutsi.platform.core.stream.Event
import org.apache.commons.text.StringEscapeUtils
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
        root.register(LIKE_STORY_COMMAND, this)
        root.register(UNLIKE_STORY_COMMAND, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            STORY_LIKED -> service.onLiked(
                objectMapper.readValue(
                    decode(event.payload),
                    StoryLikedEvent::class.java,
                ),
            )

            STORY_UNLIKED -> service.onUnliked(
                objectMapper.readValue(
                    decode(event.payload),
                    StoryUnlikedEvent::class.java,
                ),
            )

            LIKE_STORY_COMMAND -> service.like(
                objectMapper.readValue(
                    decode(event.payload),
                    LikeStoryCommand::class.java,
                ),
            )

            UNLIKE_STORY_COMMAND -> service.unlike(
                objectMapper.readValue(
                    decode(event.payload),
                    UnlikeStoryCommand::class.java,
                ),
            )

            else -> {}
        }
    }

    private fun decode(json: String): String =
        StringEscapeUtils.unescapeJson(json)
            .replace("\"{", "{")
            .replace("}\"", "}")
}
