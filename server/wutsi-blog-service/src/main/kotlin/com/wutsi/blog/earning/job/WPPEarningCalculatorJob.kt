package com.wutsi.blog.earning.job

import com.wutsi.blog.earning.service.WPPEarningService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date

@Service
class WPPEarningCalculatorJob(
    private val service: WPPEarningService,
    private val clock: Clock,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
    @Value("\${wutsi.application.wpp.monhtly-budget}") private val monthlyBudget: Long
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "wpp-earning-calculator"

    @Scheduled(cron = "\${wutsi.crontab.wpp-earning-calculator}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val date = DateUtils.toLocalDate(Date(clock.millis())).minusMonths(1)
        service.compile(date.year, date.monthValue, monthlyBudget)
        return 1L
    }
}
