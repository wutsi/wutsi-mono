package com.wutsi.blog.story.endpoint

import com.wutsi.blog.account.service.SecurityManager
import com.wutsi.blog.comment.service.CommentService
import com.wutsi.blog.like.service.LikeService
import com.wutsi.blog.share.service.ShareService
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.story.service.TopicService
import com.wutsi.blog.user.service.UserService
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetStoryQuery(
    private val service: StoryService,
    private val userService: UserService,
    private val mapper: StoryMapper,
    private val likeService: LikeService,
    private val commentService: CommentService,
    private val shareService: ShareService,
    private val topicService: TopicService,
    private val tracingContext: TracingContext,
    private val securityManager: SecurityManager,
) {
    @GetMapping("/v1/stories/{id}")
    fun get(@PathVariable id: Long): GetStoryResponse {
        val storyIds = listOf(id)
        val userId = securityManager.getCurrentUserId()

        val story = service.findById(id)
        val content = service.findContent(story, story.language)
        val topic = story.topicId?.let { topicService.findById(it) }
        val users = userService.findByIds(listOf(story.userId))

        val likes = likeService.search(
            storyIds = listOf(story.id!!),
            userId = userId,
            deviceId = tracingContext.deviceId(),
        )

        val comments = userId?.let {
            commentService.search(
                storyIds = storyIds,
                userId = userId,
            )
        } ?: emptyList()

        val shares = userId?.let {
            shareService.search(
                storyIds = storyIds,
                userId = userId,
            )
        } ?: emptyList()

        return GetStoryResponse(
            story = mapper.toStoryDto(
                story,
                content,
                topic,
                users.firstOrNull(),
                likes.firstOrNull(),
                comments.firstOrNull(),
                shares.firstOrNull(),
            ),
        )
    }
}
