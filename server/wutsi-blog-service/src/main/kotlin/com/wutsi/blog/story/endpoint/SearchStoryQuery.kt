package com.wutsi.blog.story.endpoint

import com.wutsi.blog.comment.service.CommentService
import com.wutsi.blog.like.service.LikeService
import com.wutsi.blog.share.service.ShareService
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.user.service.UserService
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
    private val mapper: StoryMapper,
    private val request: HttpServletRequest,
    private val likeService: LikeService,
    private val commentService: CommentService,
    private val shareService: ShareService,
    private val userService: UserService,
    private val tracingContext: TracingContext,
    private val securityManager: com.wutsi.blog.security.service.SecurityManager,
) {
    @PostMapping("/v1/stories/queries/search")
    fun create(@Valid @RequestBody request: SearchStoryRequest): SearchStoryResponse {
        val userId = securityManager.getCurrentUserId()

        val stories = service.search(request)
        if (stories.isEmpty()) {
            return SearchStoryResponse()
        }

        val storyIds = stories.map { it.id }.filterNotNull()
        val users = userService.findByIds(stories.map { it.userId }).associateBy { it.id }

        val likes = likeService.search(
            storyIds = storyIds,
            userId = userId,
            deviceId = tracingContext.deviceId(),
        ).associateBy { it.storyId }

        val comments = userId?.let {
            commentService.search(
                storyIds = storyIds,
                userId = userId,
            )
        }?.associateBy { it.storyId }
            ?: emptyMap()

        val shares = userId?.let {
            shareService.search(
                storyIds = storyIds,
                userId = userId,
            )
        }?.associateBy { it.storyId }
            ?: emptyMap()

        return SearchStoryResponse(
            stories = stories.map {
                mapper.toStorySummaryDto(
                    it,
                    users[it.userId],
                    likes[it.id],
                    comments[it.id],
                    shares[it.id],
                )
            },
        )
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
