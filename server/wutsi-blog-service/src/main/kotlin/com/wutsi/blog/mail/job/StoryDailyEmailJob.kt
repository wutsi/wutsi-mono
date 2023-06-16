package com.wutsi.blog.mail.job

import com.wutsi.blog.mail.service.MailService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class StoryDailyEmailJob(
    private val service: MailService,

    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    override fun getJobName() = "mail-daily"

    @Scheduled(cron = "\${wutsi.crontab.mail-daily}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        return service.sendDailyEmail()
    }
}
