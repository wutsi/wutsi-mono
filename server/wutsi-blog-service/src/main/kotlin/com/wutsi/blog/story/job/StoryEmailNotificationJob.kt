package com.wutsi.blog.story.job

import com.wutsi.blog.SortOrder
import com.wutsi.blog.event.EventType.SEND_STORY_EMAIL_NOTIFICATION_COMMAND
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.SendStoryEmailNotificationCommand
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus.PUBLISHED
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.subscription.service.SubscriptionService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class StoryEmailNotificationJob(
    private val storyService: StoryService,
    private val subscriptionService: SubscriptionService,
    private val eventStream: EventStream,
    private val logger: KVLogger,

    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryEmailNotificationJob::class.java)
    }

    override fun getJobName() = "story-notification"

    @Scheduled(cron = "\${wutsi.crontab.story-notification}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val yesterday = DateUtils.toDate(LocalDate.now().minusDays(1))
        logger.add("date", yesterday)

        val stories = storyService.searchStories(
            SearchStoryRequest(
                sortBy = StorySortStrategy.POPULARITY,
                sortOrder = SortOrder.DESCENDING,
                status = PUBLISHED,
                publishedStartDate = yesterday,
                limit = 100,
            ),
        )

        var notified = 0L
        var errors = 0L
        stories.forEach {
            try {
                if (send(it)) {
                    notified++
                }
            } catch (ex: Exception) {
                errors++
                LOGGER.info("Unable to send notification for Story#${it.id}", ex)
            }
        }
        logger.add("story_count", stories.size)
        logger.add("story_notified", notified)
        logger.add("story_errors", errors)
        return notified
    }

    private fun send(story: StoryEntity): Boolean {
        val subscriptions = subscriptionService.findSubscriptions(listOf(story.userId))
        subscriptions.forEach {
            eventStream.enqueue(
                type = SEND_STORY_EMAIL_NOTIFICATION_COMMAND,
                payload = SendStoryEmailNotificationCommand(
                    storyId = story.id!!,
                    recipientId = it.subscriberId,
                ),
            )
        }
        return true
    }
}
