package com.wutsi.marketplace.access.job

import com.wutsi.marketplace.access.service.ProductService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ImportYesterdaySalesKpiJob(
    private val service: ProductService,
    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    override fun getJobName() = "import-yesterday-sales-kpi"

    @Scheduled(cron = "\${wutsi.application.jobs.import-yesterday-sales-kpi.cron}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val now = LocalDate.now()
        return service.importSalesKpi(now.minusDays(1)) + // Yesterday
            service.importSalesKpi(now) // Today
    }
}
