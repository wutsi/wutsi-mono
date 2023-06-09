package com.wutsi.tracking.manager.job

import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.tracking.manager.service.KpiService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId

@Service
class ComputeReadsKpiJob(
    private val kpiService: KpiService,
    private val logger: KVLogger,

    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    override fun getJobName() = "compute-reads-kpi"

    @Scheduled(cron = "\${wutsi.application.jobs.compute-reads-kpi.cron}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val date = LocalDate.now(ZoneId.of("UTC"))
        logger.add("date", date)
        kpiService.computeDailyReads(date)
        kpiService.computeMonthlyReads(date)
        kpiService.computeYearlyReads(date)
        return 1
    }
}
