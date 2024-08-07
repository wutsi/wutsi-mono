package com.wutsi.blog.mail.job

import com.wutsi.blog.mail.service.sender.product.EBookNextPurchaseMailSender
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.transaction.service.TransactionService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.payment.core.Status
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date

@Service
class EBookNextPurchaseEmailJob(
    private val transactionService: TransactionService,
    private val sender: EBookNextPurchaseMailSender,
    private val logger: KVLogger,
    private val clock: Clock,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EBookNextPurchaseEmailJob::class.java)
    }

    override fun getJobName() = "ebook-next-purchase"

    @Scheduled(cron = "\${wutsi.crontab.ebook-next-purchase}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val today = DateUtils.toLocalDate(Date(clock.millis()))
        val lastWeek = DateUtils.toDate(today.minusDays(7))
        logger.add("date", lastWeek)

        // Load transactions
        val transactions = transactionService.search(
            SearchTransactionRequest(
                types = listOf(TransactionType.CHARGE),
                statuses = listOf(Status.SUCCESSFUL),
                creationDateTimeFrom = lastWeek,
                creationDateTimeTo = DateUtils.addDays(lastWeek, 1)
            )
        )
        logger.add("transaction_count", transactions.size)

        // Filter the stores
        val stores = transactions.groupBy { tx -> tx.store?.id ?: "-" }
        logger.add("store_count", stores.size)

        // Send email
        var result = 0L
        stores.forEach { store ->
            if (send(store.value[0])) {
                result++
            }
        }
        return result
    }

    private fun send(tx: TransactionEntity): Boolean {
        try {
            if (tx.product != null && tx.user != null) {
                return sender.send(tx.product, tx.user!!)
            }
        } catch (ex: Exception) {
            LOGGER.warn(
                "Unexpected error while sending email about Product#${tx.product?.id} to User#${tx.user?.id}",
                ex
            )
        }
        return false
    }
}
