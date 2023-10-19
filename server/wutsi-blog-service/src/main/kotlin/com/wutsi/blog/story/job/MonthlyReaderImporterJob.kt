package com.wutsi.blog.story.job

import com.wutsi.blog.story.service.ReaderService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MonthlyReaderImporterJob(
    private val service: ReaderService,
    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "story-readers-importer"

    @Scheduled(cron = "\${wutsi.crontab.story-readers-importer}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val now = LocalDate.now()
        val lastMonth = now.minusMonths(1)
        return service.importMonthlyReaders(now) +
            service.importMonthlyEmails(now) +
            service.importMonthlyReaders(lastMonth) +
            service.importMonthlyEmails(lastMonth)
    }
}
