package com.wutsi.checkout.access.job

import com.wutsi.checkout.access.service.BusinessService
import com.wutsi.checkout.access.service.DonationKpiService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

abstract class AbstractComputeDonationKpiJob(
    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    @Autowired
    private lateinit var service: DonationKpiService

    @Autowired
    private lateinit var businessService: BusinessService

    protected abstract fun getDate(): LocalDate

    override fun doRun(): Long {
        // The job runs every hour - compute KPIs for orders in the past xx hours
        val date = getDate()

        // Compute the KPIs
        val result = service.computeFromOrders(date)
        if (result > 0) {
            // Update business KPI
            businessService.updateDonationKpi(date)

            // Export the KPI to storage
            service.export(date)
        }
        return result
    }
}
