package com.wutsi.blog.product.job

import com.wutsi.blog.product.service.CategoryService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class CategoryStoryCountJob(
    private val service: CategoryService,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "category-story-count"

    @Scheduled(cron = "\${wutsi.crontab.category-story-count}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        service.computeStoryCount()
        return 1
    }
}
