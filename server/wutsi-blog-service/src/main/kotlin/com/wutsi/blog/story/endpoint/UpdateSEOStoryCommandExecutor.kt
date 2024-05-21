package com.wutsi.blog.story.endpoint

import com.wutsi.blog.SortOrder
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryService
import javassist.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/stories/commands/update-seo")
class UpdateSEOStoryCommandExecutor(
    private val service: StoryService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UpdateSEOStoryCommandExecutor::class.java)
    }

    @GetMapping
    fun create(
        @RequestParam(name = "user-id", required = false) userId: Long? = null,
        @RequestParam(name = "story-id", required = false) storyId: Long? = null,
    ) {
        storyId?.let { id -> updateByStory(id) }
        userId?.let { id -> updateByUserId(id) }
    }

    private fun updateByUserId(userId: Long) {
        var offset = 0
        var i = 0
        while (true) {
            val stories = service.searchStories(
                request = SearchStoryRequest(
                    status = StoryStatus.PUBLISHED,
                    userIds = listOf(userId),
                    offset = offset,
                    limit = 100,
                    sortBy = StorySortStrategy.PUBLISHED,
                    sortOrder = SortOrder.DESCENDING
                )
            )
            if (stories.isEmpty()) {
                break
            }

            stories.forEach { story ->
                update(i++, story)
            }
            offset += stories.size
        }
    }

    private fun updateByStory(storyId: Long) {
        try {
            val story = service.findById(storyId)
            update(0, story)
        } catch (ex: NotFoundException) {
            LOGGER.warn("Story not found", ex)
        }
    }

    private fun update(i: Int, story: StoryEntity) {
        try {
            LOGGER.info(">>> $i. Updating SEO information of Story#${story.id} - ${story.title}")
            service.updateSEOInformation(story)
            Thread.sleep(4000) // Pause to respect the free quota: 15 request per minute
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
        } finally {
        }
    }
}
