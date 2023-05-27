package com.wutsi.blog.comment

import com.wutsi.blog.client.comment.CountCommentResponse
import com.wutsi.blog.client.comment.CreateCommentRequest
import com.wutsi.blog.client.comment.CreateCommentResponse
import com.wutsi.blog.client.comment.GetCommentResponse
import com.wutsi.blog.client.comment.SearchCommentRequest
import com.wutsi.blog.client.comment.SearchCommentResponse
import com.wutsi.blog.client.comment.UpdateCommentRequest
import com.wutsi.blog.client.comment.UpdateCommentResponse
import com.wutsi.blog.client.event.CommentEvent
import com.wutsi.blog.comment.mapper.CommentMapper
import com.wutsi.blog.comment.service.CommentServiceV0
import org.springframework.context.ApplicationEventPublisher
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Date
import javax.validation.Valid

@RestController
@RequestMapping("/v1/comments")
class CommentController(
    private val service: CommentServiceV0,
    private val mapper: CommentMapper,
    private val events: ApplicationEventPublisher,
) {
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): GetCommentResponse =
        GetCommentResponse(
            comment = mapper.toCommentDto(
                service.findById(id),
            ),
        )

    @PostMapping
    fun create(@Valid @RequestBody request: CreateCommentRequest): CreateCommentResponse {
        val comment = service.create(request)

        events.publishEvent(
            CommentEvent(
                commenId = comment.id!!,
            ),
        )
        return CreateCommentResponse(commentId = comment.id)
    }

    @PostMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateCommentRequest,
    ): UpdateCommentResponse {
        val comment = service.update(id, request)
        return UpdateCommentResponse(commentId = comment.id!!)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @GetMapping
    fun search(
        @RequestParam(required = false) authorId: Long? = null,
        @RequestParam(required = false) storyId: List<Long>? = null,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") since: Date? = null,
        @RequestParam(required = false, defaultValue = "20") limit: Int = 20,
        @RequestParam(required = false, defaultValue = "0") offset: Int = 0,
    ): SearchCommentResponse {
        val comments = service.search(
            SearchCommentRequest(
                authorId = authorId,
                storyIds = storyId?.let { it } ?: emptyList(),
                since = since,
                offset = offset,
                limit = limit,
            ),
        )
        return SearchCommentResponse(
            comments = comments.map { mapper.toCommentDto(it) },
        )
    }

    @PostMapping("/search")
    fun search(request: SearchCommentRequest): SearchCommentResponse {
        val comments = service.search(request)
        return SearchCommentResponse(
            comments = comments.map { mapper.toCommentDto(it) },
        )
    }

    @GetMapping("/count")
    fun count(
        @RequestParam(required = false) authorId: Long? = null,
        @RequestParam(required = false) storyId: List<Long>? = null,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") since: Date? = null,
    ): CountCommentResponse {
        val counts = service.count(
            SearchCommentRequest(
                authorId = authorId,
                storyIds = storyId?.let { it } ?: emptyList(),
                since = since,
            ),
        )
        return CountCommentResponse(
            counts = counts.map { mapper.toCommentCountDto(it) },
        )
    }
}
