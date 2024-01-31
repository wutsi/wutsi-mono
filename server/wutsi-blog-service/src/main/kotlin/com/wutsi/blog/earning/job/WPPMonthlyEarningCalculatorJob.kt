package com.wutsi.blog.earning.job

import com.wutsi.blog.earning.entity.WPPStoryEntity
import com.wutsi.blog.earning.entity.WPPUserEntity
import com.wutsi.blog.earning.service.WPPEarningService
import com.wutsi.blog.mail.service.sender.earning.WPPEarningMailSender
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate
import java.util.Date

@Service
class WPPMonthlyEarningCalculatorJob(
    private val clock: Clock,
    private val service: WPPEarningService,
    private val userService: UserService,
    private val sender: WPPEarningMailSender,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
    @Value("\${wutsi.application.wpp.monhtly-budget}") private val monthlyBudget: Long,
) : AbstractCronJob(lockManager, registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WPPMonthlyEarningCalculatorJob::class.java)
    }

    override fun getJobName() = "wpp-monthly-earning-calculator"

    @Scheduled(cron = "\${wutsi.crontab.wpp-monthly-earning-calculator}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val date = DateUtils.toLocalDate(Date(clock.millis())).minusMonths(1)
        val earnings = service.compile(date.year, date.monthValue, monthlyBudget)
        earnings.users.forEach { user ->
            sendEmail(date, user, earnings.stories.filter { story -> story.userId == user.userId })
        }
        return earnings.users.size.toLong()
    }

    private fun sendEmail(date: LocalDate, user: WPPUserEntity, stories: List<WPPStoryEntity>) {
        try {
            val recipient = userService.findById(user.userId)
            sender.send(user, recipient, date, stories)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to send email to User#${user.userId}", ex)
        }
    }
}
