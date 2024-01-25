package com.wutsi.blog.mail.job

import com.wutsi.blog.mail.service.MailService
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.service.TransactionService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date

@Service
class OrderAbandonedHourlyJob(
    private val mailService: MailService,
    protected val clock: Clock,

    transactionService: TransactionService,
    logger: KVLogger,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractOrderAbandonedJob(transactionService, logger, lockManager, registry) {
    override fun getJobName() = "abandoned-order-hourly"

    override fun send(tx: TransactionEntity): Boolean =
        mailService.sendAbandonedHourlyEmail(tx) != null

    override fun fromDate(): Date =
        DateUtils.addHours(Date(clock.millis()), -1)

    override fun toDate(): Date? = null

    @Scheduled(cron = "\${wutsi.crontab.abandoned-order-hourly}")
    override fun run() {
        super.run()
    }
}
