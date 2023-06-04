package com.wutsi.blog.comment.endpoint

import com.wutsi.blog.AbstractMigrateToEventStreamCommandExecutor
import com.wutsi.blog.comment.dao.CommentV0Repository
import com.wutsi.blog.comment.domain.Comment
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.comment.service.CommentService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/comments/commands/migrate-to-event-stream")
class MigrateCommentToEventStoreCommandExecutor(
    private val dao: CommentV0Repository,
    private val service: CommentService,
    logger: KVLogger,
) : AbstractMigrateToEventStreamCommandExecutor<Comment>(logger) {
    override fun getItemsToMigrate(): List<Comment> =
        dao.findAll().toList()

    override fun migrate(item: Comment) {
        service.comment(
            CommentStoryCommand(
                storyId = item.storyId,
                userId = item.userId,
                text = item.text,
                timestamp = item.creationDateTime.time,
            ),
        )
    }
}
