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
import java.time.YearMonth
import java.util.Date

@Service
class WPPDailyEarningCalculatorJob(
    private val service: WPPEarningService,
    private val clock: Clock,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
    @Value("\${wutsi.application.wpp.monhtly-budget}") private val monthlyBudget: Long
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "wpp-daily-earning-calculator"

    @Scheduled(cron = "\${wutsi.crontab.wpp-daily-earning-calculator}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val date = DateUtils.toLocalDate(Date(clock.millis()))
        val lengthOfMonth = YearMonth.of(date.year, date.monthValue).lengthOfMonth()
        val adjustedBudget = monthlyBudget * date.dayOfMonth / lengthOfMonth // Budget adjusted base on the day of month
        val users = service.compile(date.year, date.monthValue, adjustedBudget)
        return users.size.toLong()
    }
}
