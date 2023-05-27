package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.CommentBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.comment.dto.SearchCommentRequest
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val backend: CommentBackend,
    private val requestContext: RequestContext,
) {
    fun comment(storyId: Long, text: String) {
        if (!text.isNullOrEmpty()) {
            backend.execute(
                CommentStoryCommand(
                    storyId = storyId,
                    userId = requestContext.currentUser()!!.id,
                    text = text,
                ),
            )
        }
    }

    fun search(storyIds: List<Long>) =
        backend.search(
            SearchCommentRequest(
                storyIds = storyIds,
                userId = requestContext.currentUser()?.id,
            ),
        ).comments
}
