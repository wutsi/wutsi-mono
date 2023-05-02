package com.wutsi.blog.story.service

import com.wutsi.blog.client.event.PublishEvent
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StoryStatus.draft
import com.wutsi.blog.client.story.StoryStatus.published
import com.wutsi.blog.story.domain.Story
import com.wutsi.blog.util.DateUtils
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date

@Service
class StoryPublisherJob(
    private val clock: Clock,
    private val storyService: StoryService,
    private val events: ApplicationEventPublisher,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryPublisherJob::class.java)
    }

    @Scheduled(cron = "\${wutsi.crontab.story-publisher}")
    fun run() {
        LOGGER.info("Running")

        val stories = storyService.searchStories(
            SearchStoryRequest(
                status = draft,
                scheduledPublishedEndDate = DateUtils.endOfTheDay(Date(clock.millis())),
            ),
        )

        var published = 0
        stories.forEach {
            try {
                publish(it)
                published++
            } catch (ex: Exception) {
                LOGGER.info("Unable to publish Story#${it.id}", ex)
            }
        }
        LOGGER.info("Done. ${stories.size} Stories to publish, $published published")
    }

    private fun publish(story: Story) {
        val publishedStory = storyService.publishScheduled(story)
        if (publishedStory.status == published) {
            events.publishEvent(
                PublishEvent(
                    storyId = story.id!!,
                ),
            )
        }
    }
}
