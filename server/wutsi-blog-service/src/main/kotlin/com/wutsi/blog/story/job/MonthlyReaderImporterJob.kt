package com.wutsi.blog.story.job

import com.wutsi.blog.story.service.ViewService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MonthlyReaderImporterJob(
    private val service: ViewService,
    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    override fun getJobName() = "story-readers-importer"

    @Scheduled(cron = "\${wutsi.crontab.story-readers-importer}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val now = LocalDate.now()
        return service.importMonthlyReaders(now) + service.importMonthlyReaders(now.minusMonths(1))
    }
}
