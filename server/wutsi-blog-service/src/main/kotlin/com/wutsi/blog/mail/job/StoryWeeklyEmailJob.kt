package com.wutsi.blog.mail.job

import com.wutsi.blog.mail.service.MailService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class StoryWeeklyEmailJob(
    private val mailService: MailService,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryWeeklyEmailJob::class.java)
    }

    override fun getJobName() = "mail-weekly"

    @Scheduled(cron = "\${wutsi.crontab.mail-weekly}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        try {
            mailService.sendWeekly()
        } catch (ex: Exception) {
            LOGGER.warn("Unable to send the weekly email", ex)
        }
        return 1
    }
}
