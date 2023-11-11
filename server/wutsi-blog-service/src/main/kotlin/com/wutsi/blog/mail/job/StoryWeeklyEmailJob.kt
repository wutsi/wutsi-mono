package com.wutsi.blog.mail.job

import com.wutsi.blog.event.EventType
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.stream.EventStream
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class StoryWeeklyEmailJob(
    private val eventStream: EventStream,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "mail-weekly"

    @Scheduled(cron = "\${wutsi.crontab.mail-weekly}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        eventStream.enqueue(
            type = EventType.SEND_STORY_WEEKLY_EMAIL_COMMAND,
            payload = "",
        )
        return 1
    }
}
