package com.wutsi.blog.comment.endpoint

import com.wutsi.blog.comment.dao.CommentRepository
import com.wutsi.blog.comment.domain.CommentEntity
import com.wutsi.blog.comment.dto.Comment
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.comment.dto.SearchCommentRequest
import com.wutsi.blog.comment.dto.SearchCommentResponse
import com.wutsi.event.store.EventStore
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
    private val eventStore: EventStore,
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

        // Events
        val payloadByEventId = eventStore.events(comments.map { it.eventId })
            .associate { it.id to it.payload }

        return SearchCommentResponse(
            comments = comments.map {
                toComment(it, payloadByEventId)
            }.filterNotNull()
        )
    }

    private fun limit(request: SearchCommentRequest): Int =
        if (request.limit == 0) {
            20
        } else {
            request.limit
        }

    private fun toComment(comment: CommentEntity, payloads: Map<String, Any?>): Comment? {
        val payload = payloads[comment.eventId] ?: return null

        return Comment(
            id = comment.id ?: -1,
            userId = comment.userId,
            storyId = comment.storyId,
            timestamp = comment.timestamp,
            text = (payload as CommentStoryCommand).text,
        )
    }
}
