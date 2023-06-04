package com.wutsi.blog.story

import com.wutsi.blog.client.event.PublishEvent
import com.wutsi.blog.client.story.CountStoryResponse
import com.wutsi.blog.client.story.GetStoryReadabilityResponse
import com.wutsi.blog.client.story.GetStoryResponse
import com.wutsi.blog.client.story.PublishStoryRequest
import com.wutsi.blog.client.story.PublishStoryResponse
import com.wutsi.blog.client.story.RecommendStoryRequest
import com.wutsi.blog.client.story.RecommendStoryResponse
import com.wutsi.blog.client.story.SaveStoryRequest
import com.wutsi.blog.client.story.SaveStoryResponse
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SearchStoryResponse
import com.wutsi.blog.client.story.SortStoryRequest
import com.wutsi.blog.client.story.SortStoryResponse
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus.published
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.story.service.TopicService
import com.wutsi.blog.story.service.sort.SortService
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/story")
class StoryController(
    private val storyService: StoryService,
    private val sortService: SortService,
    private val topicService: TopicService,
    private val mapper: StoryMapper,
    private val events: ApplicationEventPublisher,
) {
    @GetMapping("/{id}")
    fun story(@PathVariable id: Long): GetStoryResponse {
        val story = storyService.findById(id)
        val content = storyService.findContent(story, story.language)
        val topic = story.topicId?.let { topicService.findById(it) }
        return GetStoryResponse(
            story = mapper.toStoryDto(story, content, topic),
        )
    }

    @PostMapping()
    fun create(@RequestBody @Valid request: SaveStoryRequest): SaveStoryResponse =
        storyService.create(request)

    @PostMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody @Valid request: SaveStoryRequest): SaveStoryResponse =
        storyService.update(id, request)

    @PostMapping("/{id}/publish")
    fun publish(
        @PathVariable id: Long,
        @RequestBody @Valid request: PublishStoryRequest,
    ): PublishStoryResponse {
        val story = storyService.findById(id)
        val previousStatus = story.status

        val publishedStory = storyService.publish(story, request)
        if (publishedStory.status == published && publishedStory.status != previousStatus) {
            events.publishEvent(
                PublishEvent(
                    storyId = id,
                ),
            )
        }
        return PublishStoryResponse(
            storyId = id,
        )
    }

    @PostMapping("/search")
    fun search(
        @RequestBody @Valid request: SearchStoryRequest,
        @RequestHeader(name = TracingContext.HEADER_DEVICE_ID) deviceId: String? = null,
    ): SearchStoryResponse =
        storyService.search(request, deviceId)

    @PostMapping("/count")
    fun count(@RequestBody @Valid request: SearchStoryRequest): CountStoryResponse =
        storyService.count(request)

    @GetMapping("/{id}/readability")
    fun readability(@PathVariable id: Long): GetStoryReadabilityResponse =
        storyService.readability(id)

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
    ) {
        storyService.delete(id)
    }

    @PostMapping("/sort")
    fun sort(@RequestBody @Valid request: SortStoryRequest): SortStoryResponse =
        sortService.sort(request)

    @PostMapping("/recommend")
    fun recommend(@RequestBody @Valid request: RecommendStoryRequest): RecommendStoryResponse {
        val story = storyService.findById(request.storyId ?: -1)
        val response = search(
            request = SearchStoryRequest(
                limit = request.limit + 1,
                status = published,
                context = request.context,
                sortBy = StorySortStrategy.recommended,
                userIds = listOf(story.userId),
            ),
        )
        return RecommendStoryResponse(
            stories = response.stories.filter { it.id != request.storyId },
        )
    }
}
