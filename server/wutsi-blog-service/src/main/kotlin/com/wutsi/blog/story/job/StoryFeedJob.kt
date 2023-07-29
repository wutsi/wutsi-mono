package com.wutsi.blog.story.job

import com.wutsi.blog.story.service.StoryFeedsService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class StoryFeedJob(
    private val service: StoryFeedsService,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "story-feed"

    @Scheduled(cron = "\${wutsi.crontab.story-feed}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        return service.generate()
    }
}
