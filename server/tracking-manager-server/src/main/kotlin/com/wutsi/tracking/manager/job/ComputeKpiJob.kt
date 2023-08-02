package com.wutsi.tracking.manager.job

import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.tracking.manager.service.KpiService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId

@Service
class ComputeKpiJob(
    private val kpiService: KpiService,
    private val logger: KVLogger,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "compute-kpi"

    @Scheduled(cron = "\${wutsi.application.jobs.compute-kpi.cron}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val date = LocalDate.now(ZoneId.of("UTC"))
        logger.add("date", date)

        kpiService.computeDaily(date)

        kpiService.computeMonthly(date)

        kpiService.computeYearly(date)
        return 1
    }
}
