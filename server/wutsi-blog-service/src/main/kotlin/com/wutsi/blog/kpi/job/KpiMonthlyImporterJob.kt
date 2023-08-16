package com.wutsi.blog.kpi.job

import com.wutsi.blog.kpi.service.KpiService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class KpiMonthlyImporterJob(
    private val kpiService: KpiService,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "kpi-monthly-importer"

    @Scheduled(cron = "\${wutsi.crontab.kpi-monthly-importer}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        return kpiService.import(LocalDate.now())
    }
}
