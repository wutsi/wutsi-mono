package com.wutsi.tracking.manager.job

import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.tracking.manager.service.KpiService
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId

@Service
class ComputeDailyViewsKpiJob(
    private val kpiService: KpiService,
    private val logger: KVLogger,
    @Value("\${wutsi.application.jobs.compute-daily-views-kpi.enabled}") private val enabled: Boolean,

    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    override fun getJobName() = "compute-daily-views-kpi"

    @Scheduled(cron = "\${wutsi.application.jobs.compute-daily-views-kpi.cron}")
    override fun run() {
        logger.add("enabled", enabled)
        if (!enabled) {
            return
        }
        super.run()
    }

    override fun doRun(): Long {
        val date = LocalDate.now(ZoneId.of("UTC"))
        logger.add("date", date)
        kpiService.computeDailyViews(date)
        return 1
    }
}
