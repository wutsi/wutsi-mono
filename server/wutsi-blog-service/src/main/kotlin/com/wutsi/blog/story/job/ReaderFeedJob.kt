package com.wutsi.blog.story.job

import com.wutsi.blog.story.service.ReaderFeedService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.stereotype.Service

@Service
class ReaderFeedJob(
    private val service: ReaderFeedService,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "reader-feed"

    override fun doRun(): Long {
        return service.generate()
    }
}
