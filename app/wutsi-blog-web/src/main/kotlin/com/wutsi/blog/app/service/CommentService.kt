package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.CommentBackend
import com.wutsi.blog.app.common.service.Moment
import com.wutsi.blog.app.form.CreateCommentForm
import com.wutsi.blog.app.model.CommentModel
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.comment.dto.SearchCommentRequest
import org.apache.commons.lang.StringEscapeUtils
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val backend: CommentBackend,
    private val userService: UserService,
    private val requestContext: RequestContext,
    private val moment: Moment,
) {
    fun create(form: CreateCommentForm) {
        if (!form.text.trim().isNullOrEmpty()) {
            backend.execute(
                CommentStoryCommand(
                    storyId = form.storyId,
                    userId = requestContext.currentUser()!!.id,
                    text = form.text.trim(),
                ),
            )
        }
    }

    fun search(storyId: Long, limit: Int, offset: Int): List<CommentModel> {
        val comments = backend.search(
            SearchCommentRequest(
                storyId = storyId,
                limit = limit,
                offset = offset,
            ),
        ).comments

        val userIds = comments.map { it.userId }.toSet().toList()
        val users = userService.search(
            SearchUserRequest(
                userIds = userIds,
                limit = userIds.size,
            ),
        ).associateBy { it.id }
        return comments.map {
            CommentModel(
                id = it.id,
                text = it.text,
                html = toHtml(it.text),
                timestamp = moment.format(it.timestamp),
                user = users[it.userId],
            )
        }
    }

    private fun toHtml(text: String): String {
        val html = StringEscapeUtils.escapeHtml(text)
        return html.replace("\n", "<br/>")
    }
}
