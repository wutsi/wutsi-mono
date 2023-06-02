package com.wutsi.blog.comment.endpoint

import com.wutsi.blog.comment.dao.CommentRepository
import com.wutsi.blog.comment.domain.CommentEntity
import com.wutsi.blog.comment.dto.Comment
import com.wutsi.blog.comment.dto.SearchCommentRequest
import com.wutsi.blog.comment.dto.SearchCommentResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/comments/queries/search")
class SearchCommentQuery(
    private val commentDao: CommentRepository,
) {
    @PostMapping
    fun search(
        @Valid @RequestBody request: SearchCommentRequest,
    ): SearchCommentResponse {
        // Comments
        val limit = limit(request)
        val pageable = PageRequest.of(
            request.offset / limit,
            limit,
            Sort.by(Sort.Order.desc("timestamp")),
        )
        val comments = commentDao.findByStoryId(request.storyId, pageable)

        return SearchCommentResponse(
            comments = comments.map {
                toComment(it)
            }.filterNotNull(),
        )
    }

    private fun limit(request: SearchCommentRequest): Int =
        if (request.limit == 0) {
            20
        } else {
            request.limit
        }

    private fun toComment(comment: CommentEntity) = Comment(
        id = comment.id ?: -1,
        userId = comment.userId,
        storyId = comment.storyId,
        timestamp = comment.timestamp,
        text = comment.text
    )
}
