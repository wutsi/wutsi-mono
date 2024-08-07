package com.wutsi.blog.like.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.LIKE_STORY_COMMAND
import com.wutsi.blog.event.EventType.STORY_LIKED_EVENT
import com.wutsi.blog.event.EventType.STORY_UNLIKED_EVENT
import com.wutsi.blog.event.EventType.UNLIKE_STORY_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.UnlikeStoryCommand
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.Event
import org.apache.commons.text.StringEscapeUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class LikeEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val service: LikeService,
    private val logger: KVLogger,
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(STORY_LIKED_EVENT, this)
        root.register(STORY_UNLIKED_EVENT, this)
        root.register(LIKE_STORY_COMMAND, this)
        root.register(UNLIKE_STORY_COMMAND, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            STORY_LIKED_EVENT -> service.onLiked(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            STORY_UNLIKED_EVENT -> service.onUnliked(
                objectMapper.readValue(
                    decode(event.payload),
                    EventPayload::class.java,
                ),
            )

            LIKE_STORY_COMMAND -> {
                try {
                    service.like(
                        objectMapper.readValue(
                            decode(event.payload),
                            LikeStoryCommand::class.java,
                        ),
                    )
                } catch (e: DataIntegrityViolationException) {
                    // Ignore - duplicate like
                    logger.add("duplicate_like", true)
                }
            }

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
        StringEscapeUtils
            .unescapeJson(json)
            .replace("\"{", "{")
            .replace("}\"", "}")
}
