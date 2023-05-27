package com.wutsi.blog.app.component.comment.service

import com.wutsi.blog.app.backend.CommentBackendV0
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.component.comment.model.CommentCountModel
import com.wutsi.blog.app.component.comment.model.CommentModel
import com.wutsi.blog.app.component.comment.model.CreateCommentForm
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.client.comment.CreateCommentRequest
import com.wutsi.blog.client.comment.SearchCommentRequest
import com.wutsi.blog.client.user.SearchUserRequest
import org.springframework.stereotype.Service

@Deprecated("")
@Service
class CommentServiceV0(
    private val api: CommentBackendV0,
    private val mapper: CommentMapper,
    private val users: UserService,
    private val requestContext: RequestContext,
) {
    fun count(storyIds: List<Long>): List<CommentCountModel> {
        return count(
            SearchCommentRequest(
                storyIds = storyIds,
            ),
        )
    }

    fun count(request: SearchCommentRequest): List<CommentCountModel> {
        val counts = api.count(request).counts

        return counts.map { mapper.toCommentCountModel(it) }
    }

    fun list(storyId: Long, limit: Int = 50, offset: Int = 0): List<CommentModel> {
        val comments = api.search(
            SearchCommentRequest(
                storyIds = listOf(storyId),
                limit = limit,
                offset = offset,
            ),
        ).comments

        val userIds = comments.map { it.userId }.toSet()
        val users = users.search(
            SearchUserRequest(
                userIds = userIds.toList(),
                limit = userIds.size,
                offset = 0,
            ),
        ).map { it.id to it }.toMap()

        return comments.map { mapper.toCommentModel(it, users) }
    }

    fun create(form: CreateCommentForm): Long {
        return api.create(
            CreateCommentRequest(
                storyId = form.storyId,
                text = form.text,
                userId = requestContext.currentUser()!!.id,
            ),
        ).commentId
    }
}
