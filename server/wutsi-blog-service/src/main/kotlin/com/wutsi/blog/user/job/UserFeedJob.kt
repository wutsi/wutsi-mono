package com.wutsi.blog.user.job

import com.wutsi.blog.user.service.UserFeedService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class UserFeedJob(
    private val service: UserFeedService,

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
