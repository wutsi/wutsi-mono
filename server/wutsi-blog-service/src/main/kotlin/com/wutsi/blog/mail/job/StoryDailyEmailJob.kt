package com.wutsi.blog.story.job

import com.wutsi.blog.story.service.StoryService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class StoryDailyEmailJob(
    private val service: StoryService,

    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryDailyEmailJob::class.java)
    }

    override fun getJobName() = "story-daily-email"

    @Scheduled(cron = "\${wutsi.crontab.story-daily-email}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        return service.sendDailyEmail()
    }
}
