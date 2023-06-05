package com.wutsi.blog.story.job

import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.story.dto.PublishStoryCommand
import com.wutsi.blog.story.dto.StoryStatus.DRAFT
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.util.DateUtils
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date

@Service
class StoryPublisherJob(
    private val clock: Clock,
    private val storyService: StoryService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryPublisherJob::class.java)
    }

    @Scheduled(cron = "\${wutsi.crontab.story-publisher}")
    fun run() {
        LOGGER.info("Running")
        val stories = storyService.searchStories(
            SearchStoryRequest(
                status = DRAFT,
                scheduledPublishedEndDate = DateUtils.endOfTheDay(Date(clock.millis())),
            ),
        )

        var published = 0
        var errors = 0
        stories.forEach {
            try {
                storyService.publish(
                    PublishStoryCommand(
                        storyId = it.id!!,
                    ),
                )
                published++
            } catch (ex: Exception) {
                errors = 0
                LOGGER.info("Unable to publish Story#${it.id}", ex)
            }
        }
        LOGGER.info("Done. ${stories.size} Stories to publish, $published published, $errors errors(s)")
    }
}
