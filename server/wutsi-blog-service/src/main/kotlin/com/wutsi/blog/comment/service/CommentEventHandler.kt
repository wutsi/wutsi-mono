package com.wutsi.blog.comment.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.event.EventHandler
import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.COMMENT_STORY_COMMAND
import com.wutsi.blog.event.EventType.STORY_COMMENTED_EVENT
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.platform.core.stream.Event
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class CommentEventHandler(
    private val root: RootEventHandler,
    private val objectMapper: ObjectMapper,
    private val service: CommentService,
) : EventHandler {
    @PostConstruct
    fun init() {
        root.register(STORY_COMMENTED_EVENT, this)
        root.register(COMMENT_STORY_COMMAND, this)
    }

    override fun handle(event: Event) {
        when (event.type) {
            STORY_COMMENTED_EVENT -> service.onCommented(
                objectMapper.readValue(
                    event.payload,
                    EventPayload::class.java,
                ),
            )

            COMMENT_STORY_COMMAND -> service.comment(
                objectMapper.readValue(
                    event.payload,
                    CommentStoryCommand::class.java,
                ),
            )

            else -> {}
        }
    }
}
