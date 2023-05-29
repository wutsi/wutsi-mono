package com.wutsi.blog.comment.endpoint

import com.wutsi.blog.comment.dao.CommentRepository
import com.wutsi.blog.comment.dao.CommentStoryRepository
import com.wutsi.blog.comment.dto.CommentCounter
import com.wutsi.blog.comment.dto.CountCommentRequest
import com.wutsi.blog.comment.dto.CountCommentResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/comments/queries/count")
class CountCommentQuery(
    private val storyDao: CommentStoryRepository,
    private val commentDao: CommentRepository,
) {
    @PostMapping
    fun search(
        @Valid @RequestBody request: CountCommentRequest,
    ): CountCommentResponse {
        // Stories
        val stories = storyDao.findAllById(request.storyIds.toSet()).toList()
        if (stories.isEmpty()) {
            return CountCommentResponse()
        }

        // Liked stories
        val comments: List<Long> = if (request.userId != null) {
            commentDao.findByStoryIdInAndUserId(stories.map { it.storyId }, request.userId!!).map { it.storyId }
        } else {
            emptyList()
        }

        // Result
        return CountCommentResponse(
            commentStories = stories.map {
                CommentCounter(
                    storyId = it.storyId,
                    count = it.count,
                    commented = comments.contains(it.storyId),
                )
            },
        )
    }
}
