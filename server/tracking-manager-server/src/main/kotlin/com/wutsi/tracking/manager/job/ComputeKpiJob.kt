package com.wutsi.tracking.manager.job

import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.tracking.manager.service.KpiService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@Service
class ComputeKpiJob(
    private val kpiService: KpiService,
    private val logger: KVLogger,
    private val clock: Clock,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "compute-kpi"

    @Scheduled(cron = "\${wutsi.application.jobs.compute-kpi.cron}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val date = Instant.ofEpochMilli(clock.millis()).atZone(ZoneOffset.UTC).toLocalDate()
        logger.add("date", date)

        kpiService.computeDaily(date)

        kpiService.computeMonthly(date)

        kpiService.computeYearly(date)
        return 1
    }
}
