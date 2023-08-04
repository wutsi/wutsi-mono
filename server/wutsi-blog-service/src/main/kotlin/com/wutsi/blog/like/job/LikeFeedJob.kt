package com.wutsi.blog.like.job

import com.wutsi.blog.like.service.LikeFeedService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class LikeFeedJob(
    private val service: LikeFeedService,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "like-feed"

    @Scheduled(cron = "\${wutsi.crontab.like-feed}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        return service.generate()
    }
}
