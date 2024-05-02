package com.wutsi.blog.transaction.job

import com.wutsi.blog.event.EventType.TRANSACTION_NOTIFICATION_SUBMITTED_EVENT
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.SubmitTransactionNotificationCommand
import com.wutsi.blog.transaction.service.TransactionService
import com.wutsi.blog.transaction.service.WalletService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.payment.core.Status
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date

@Service
class TransactionPendingJob(
    private val service: WalletService,
    private val transactionService: TransactionService,
    private val eventStream: EventStream,
    private val logger: KVLogger,
    private val clock: Clock,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TransactionPendingJob::class.java)
    }

    override fun getJobName() = "transaction-pending"

    @Scheduled(cron = "\${wutsi.crontab.transaction-pending}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val now = Date(clock.millis())
        var count = 0L
        while (true) {
            val txs = transactionService.search(
                SearchTransactionRequest(
                    statuses = listOf(Status.PENDING),
                    creationDateTimeTo = DateUtils.addHours(now, -1)
                )
            )
            if (txs.isEmpty()) {
                break
            }

            txs.forEach { tx ->
                eventStream.enqueue(
                    TRANSACTION_NOTIFICATION_SUBMITTED_EVENT,
                    SubmitTransactionNotificationCommand(
                        transactionId = tx.id ?: "-",
                        timestamp = now.time
                    )
                )
                count++
            }
        }
        return count
    }
}
