package com.wutsi.blog.story.endpoint

import com.wutsi.blog.account.service.AuthenticationService
import com.wutsi.blog.comment.dto.CountCommentRequest
import com.wutsi.blog.comment.service.CommentService
import com.wutsi.blog.like.dto.CountLikeRequest
import com.wutsi.blog.like.service.LikeService
import com.wutsi.blog.pin.dto.SearchPinRequest
import com.wutsi.blog.pin.service.PinService
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.story.service.TopicService
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping
class GetStoryQuery(
    private val service: StoryService,
    private val pinService: PinService,
    private val mapper: StoryMapper,
    private val request: HttpServletRequest,
    private val authService: AuthenticationService,
    private val likeService: LikeService,
    private val commentService: CommentService,
    private val topicService: TopicService,
    private val tracingContext: TracingContext,
) {
    @GetMapping("/v1/stories/{id}")
    fun create(@PathVariable id: Long): GetStoryResponse {
        val storyIds = listOf(id)
        val userId = getCurrentUserId()
        val userIds = userId?.let { listOf(it) } ?: emptyList()

        val story = service.findById(id)
        val content = service.findContent(story, story.language)
        val topic = story.topicId?.let { topicService.findById(it) }
        val pins = pinService.search(SearchPinRequest(userIds))
        val likes = likeService.count(CountLikeRequest(storyIds = storyIds, deviceId = tracingContext.deviceId()))
        val comments = commentService.count(CountCommentRequest(storyIds = storyIds, userId = userId))

        return GetStoryResponse(
            story = mapper.toStoryDto(
                story,
                content,
                topic,
                pins.firstOrNull(),
                likes.counters.firstOrNull(),
                comments.counters.firstOrNull()
            )
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
