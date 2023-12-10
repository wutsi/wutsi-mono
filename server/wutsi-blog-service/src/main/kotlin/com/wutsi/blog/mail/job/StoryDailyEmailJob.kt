package com.wutsi.blog.mail.job

import com.wutsi.blog.SortOrder
import com.wutsi.blog.mail.dto.SendStoryDailyEmailCommand
import com.wutsi.blog.mail.service.MailService
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date

@Service
class StoryDailyEmailJob(
    private val storyService: StoryService,
    private val logger: KVLogger,
    private val clock: Clock,
    private val mailService: MailService,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryDailyEmailJob::class.java)
    }

    override fun getJobName() = "mail-daily"

    @Scheduled(cron = "\${wutsi.crontab.mail-daily}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val today = DateUtils.toLocalDate(Date(clock.millis()))
        val yesterday = DateUtils.toDate(today.minusDays(1))
        logger.add("date", yesterday)

        val stories = storyService.searchStories(
            SearchStoryRequest(
                sortBy = StorySortStrategy.POPULARITY,
                sortOrder = SortOrder.DESCENDING,
                status = StoryStatus.PUBLISHED,
                publishedStartDate = yesterday,
                limit = 100,
            ),
        )
        logger.add("story_count", stories.size)

        stories.forEach { story ->
            try {
                mailService.sendDaily(SendStoryDailyEmailCommand(storyId = story.id ?: -1))
                storyService.onDailyEmailSent(story)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to send the daily email for Story#${story.id}", ex)
            }
        }
        return stories.size.toLong()
    }
}
