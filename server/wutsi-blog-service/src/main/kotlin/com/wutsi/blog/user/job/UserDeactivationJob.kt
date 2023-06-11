package com.wutsi.blog.story.job

import com.wutsi.blog.story.dto.PublishStoryCommand
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StoryStatus.DRAFT
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date

@Service
class StoryPublisherJob(
    private val clock: Clock,
    private val storyService: StoryService,
    private val logger: KVLogger,

    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryPublisherJob::class.java)
    }

    override fun getJobName() = "story-publisherr"

    @Scheduled(cron = "\${wutsi.crontab.job.story-publisher}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val stories = storyService.searchStories(
            SearchStoryRequest(
                status = DRAFT,
                scheduledPublishedEndDate = DateUtils.endOfTheDay(Date(clock.millis())),
            ),
        )

        var published = 0L
        var errors = 0L
        stories.forEach {
            try {
                storyService.publish(
                    PublishStoryCommand(
                        storyId = it.id!!,
                    ),
                )
                published++
            } catch (ex: Exception) {
                errors++
                LOGGER.info("Unable to publish Story#${it.id}", ex)
            }
        }
        logger.add("story_count", stories.size)
        logger.add("story_published", published)
        logger.add("story_errors", errors)
        return published
    }
}
