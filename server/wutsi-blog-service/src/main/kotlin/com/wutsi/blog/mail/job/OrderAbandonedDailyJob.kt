package com.wutsi.blog.mail.job

import com.wutsi.blog.event.EventType
import com.wutsi.blog.mail.service.sender.transaction.OrderAbandonedMailSender
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.service.TransactionService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.payment.core.Status
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date

@Service
class OrderAbandonedDailyJob(
    protected val clock: Clock,
    private val sender: OrderAbandonedMailSender,

    transactionService: TransactionService,
    logger: KVLogger,
    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractOrderAbandonedJob(transactionService, logger, lockManager, registry) {
    override fun getJobName() = "order-abandoned-daily"

    override fun send(tx: TransactionEntity): Boolean {
        val result = if (tx.status == Status.FAILED) {
            sender.send(tx, EventType.TRANSACTION_ABANDONED_DAILY_EMAIL_SENT_EVENT)
        } else {
            null
        }
        return result != null
    }

    override fun fromDate(): Date =
        DateUtils.toDate(
            DateUtils.toLocalDate(Date(clock.millis())).minusDays(1)
        )

    override fun toDate(): Date? =
        DateUtils.toDate(
            DateUtils.toLocalDate(Date(clock.millis()))
        )

    @Scheduled(cron = "\${wutsi.crontab.order-abandoned-daily}")
    override fun run() {
        super.run()
    }
}
