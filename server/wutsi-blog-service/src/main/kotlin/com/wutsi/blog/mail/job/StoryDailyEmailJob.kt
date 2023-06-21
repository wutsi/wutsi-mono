package com.wutsi.blog.mail.job

import com.wutsi.blog.SortOrder
import com.wutsi.blog.event.EventType
import com.wutsi.blog.mail.dto.SendStoryDailyEmailCommand
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId

@Service
class StoryDailyEmailJob(
    private val storyService: StoryService,
    private val logger: KVLogger,
    private val eventStream: EventStream,

    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    override fun getJobName() = "mail-daily"

    @Scheduled(cron = "\${wutsi.crontab.mail-daily}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val yesterday = DateUtils.toDate(LocalDate.now(ZoneId.of("UTC")).minusDays(1))
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

        stories.forEach {
            eventStream.enqueue(
                type = EventType.SEND_STORY_DAILY_EMAIL_COMMAND,
                payload = SendStoryDailyEmailCommand(
                    storyId = it.id!!,
                ),
            )
        }
        return stories.size.toLong()
    }
}
