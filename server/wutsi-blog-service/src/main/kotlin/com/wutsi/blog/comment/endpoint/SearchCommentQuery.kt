package com.wutsi.blog.comment.endpoint

import com.wutsi.blog.comment.dao.CommentRepository
import com.wutsi.blog.comment.dao.CommentStoryRepository
import com.wutsi.blog.comment.dto.CommentStory
import com.wutsi.blog.comment.dto.SearchCommentRequest
import com.wutsi.blog.comment.dto.SearchCommentResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/comments/queries/search")
class SearchCommentQuery(
    private val storyDao: CommentStoryRepository,
    private val commentDao: CommentRepository,
) {
    @PostMapping
    fun search(
        @Valid @RequestBody request: SearchCommentRequest,
    ): SearchCommentResponse {
        // Stories
        val stories = storyDao.findAllById(request.storyIds.toSet()).toList()
        if (stories.isEmpty()) {
            return SearchCommentResponse()
        }

        // Liked stories
        val comments: List<Long> = if (request.userId != null) {
            commentDao.findByStoryIdInAndUserId(stories.map { it.storyId }, request.userId!!).map { it.storyId }
        } else {
            emptyList()
        }

        // Result
        return SearchCommentResponse(
            comments = stories.map {
                CommentStory(
                    storyId = it.storyId,
                    count = it.count,
                    commented = comments.contains(it.storyId),
                )
            },
        )
    }
}
