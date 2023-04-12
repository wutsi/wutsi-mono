package com.wutsi.checkout.access.job

import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ComputeYesterdayDonationKpiJob(
    lockManager: CronLockManager,
) : AbstractComputeDonationKpiJob(lockManager) {
    override fun getDate(): LocalDate = LocalDate.now().minusDays(1)

    override fun getJobName() = "compute-yesterday-donation-kpi"

    @Scheduled(cron = "\${wutsi.application.jobs.compute-today-donation-kpi.cron}")
    override fun run() {
        super.run()
    }
}
