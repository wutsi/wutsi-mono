package com.wutsi.blog.story.endpoint

import com.wutsi.blog.account.service.AuthenticationService
import com.wutsi.blog.comment.dto.CountCommentRequest
import com.wutsi.blog.comment.service.CommentService
import com.wutsi.blog.like.dto.CountLikeRequest
import com.wutsi.blog.like.service.LikeService
import com.wutsi.blog.pin.dto.SearchPinRequest
import com.wutsi.blog.pin.service.PinService
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.story.service.StoryService
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping
class SearchStoryQuery(
    private val service: StoryService,
    private val pinService: PinService,
    private val mapper: StoryMapper,
    private val request: HttpServletRequest,
    private val authService: AuthenticationService,
    private val likeService: LikeService,
    private val commentService: CommentService,
    private val tracingContext: TracingContext,
) {
    @PostMapping("/v1/stories/queries/search")
    fun create(@Valid @RequestBody request: SearchStoryRequest): SearchStoryResponse {
        val userId = getCurrentUserId()
        val userIds = userId?.let { listOf(it) } ?: emptyList()

        val stories = service.search(request)
        if (stories.isEmpty()) {
            return SearchStoryResponse()
        }

        val storyIds = stories.map { it.id }.filterNotNull()
        val pins = pinService.search(SearchPinRequest(userIds)).associateBy { it.storyId }
        val likes = likeService.count(
            CountLikeRequest(
                storyIds = storyIds,
                deviceId = tracingContext.deviceId()
            )
        ).counters.associateBy { it.storyId }
        val comments = commentService.count(
            CountCommentRequest(
                storyIds = storyIds,
                userId = userId
            )
        ).counters.associateBy { it.storyId }

        return SearchStoryResponse(
            stories = stories.map {
                mapper.toStorySummaryDto(
                    it,
                    pins[it.id],
                    likes[it.id],
                    comments[it.id],
                )
            }
        )
    }

    private fun getCurrentUserId(): Long? {
        try {
            val token = getToken()
            return token?.let {
                authService.findByAccessToken(token).account.user.id
            }
        } catch (ex: Exception) {
            return null
        }
    }

    private fun getToken(): String? {
        val value = request.getHeader("Authorization") ?: return null
        return if (value.startsWith("Bearer ", ignoreCase = true)) {
            value.substring(7)
        } else {
            null
        }
    }
}
