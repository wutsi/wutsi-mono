package com.wutsi.blog.comment.endpoint

import com.wutsi.blog.comment.dao.CommentV0Repository
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.event.EventType
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/comments/commands/migrate-to-event-stream")
class MigrateCommentToEventStoreCommand(
    private val dao: CommentV0Repository,
    private val eventStream: EventStream,
    private val logger: KVLogger,
) {
    @Async
    @GetMapping
    fun migrate() {
        val comments = dao.findAll()
        logger.add("comment_count", comments.toList().size)

        var migrated = 0
        comments.forEach {
            eventStream.enqueue(
                type = EventType.COMMENT_STORY_COMMAND,
                payload = CommentStoryCommand(
                    storyId = it.storyId,
                    userId = it.userId,
                    text = it.text,
                    timestamp = it.creationDateTime.time,
                ),
            )
            migrated++
        }
        logger.add("comment_migrated", migrated)
    }
}
