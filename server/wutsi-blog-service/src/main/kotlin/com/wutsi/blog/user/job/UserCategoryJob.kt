package com.wutsi.blog.user.job

import com.wutsi.blog.user.service.UserService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class UserCategoryJob(
    private val service: UserService,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "user-category"

    @Scheduled(cron = "\${wutsi.crontab.user-category}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        service.computeCategory()
        return 1L
    }
}
