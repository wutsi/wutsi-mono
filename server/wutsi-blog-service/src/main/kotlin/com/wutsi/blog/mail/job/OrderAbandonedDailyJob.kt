package com.wutsi.blog.mail.job

import com.wutsi.blog.mail.service.sender.transaction.OrderAbandonedMailSender
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.transaction.service.TransactionService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Status
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date

@Service
class OrderAbandonedDailyJob(
    protected val clock: Clock,
    private val sender: OrderAbandonedMailSender,
    private val transactionService: TransactionService,
    private val logger: KVLogger,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "order-abandoned"

    @Scheduled(cron = "\${wutsi.crontab.order-abandoned}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val to = DateUtils.toDate(
            DateUtils.toLocalDate(Date(clock.millis())).minusDays(1)
        )
        val from = DateUtils.toDate(
            DateUtils.toLocalDate(Date(clock.millis())).minusDays(2)
        )
        logger.add("from_date", from)
        logger.add("to_date", to)

        val txs = dedupByUser(findTransactions(from, to))
        logger.add("transaction_count", txs.size)

        var result = 0L
        txs.forEach { tx ->
            if (isAbandoned(tx) && send(tx)) {
                result++
            }
        }
        return result
    }

    private fun send(tx: TransactionEntity): Boolean {
        val result = if (tx.status == Status.FAILED) {
            sender.send(tx)
        } else {
            null
        }
        return result != null
    }

    private fun isAbandoned(tx: TransactionEntity): Boolean {
        val txs = transactionService.search(
            SearchTransactionRequest(
                types = listOf(tx.type),
                statuses = listOf(Status.SUCCESSFUL, Status.PENDING),
                productIds = tx.product?.id?.let { productId -> listOf(productId) } ?: emptyList(),
                creationDateTimeFrom = tx.creationDateTime,
                userId = tx.user?.id,
                email = if (tx.user?.id == null) tx.email else null,
                storeId = tx.store?.id,
                walletId = tx.wallet?.id,
            )
        )
        return txs.isEmpty()
    }

    private fun dedupByUser(txs: List<TransactionEntity>): List<TransactionEntity> {
        val emails = mutableSetOf<String>()
        val result = mutableListOf<TransactionEntity>()
        txs.forEach { tx ->
            val email = (tx.user?.email ?: tx.email)?.lowercase()
            if (email != null && emails.add(email)) {
                result.add(tx)
            }
        }
        return result
    }

    private fun findTransactions(from: Date, to: Date): List<TransactionEntity> =
        transactionService
            .search(
                SearchTransactionRequest(
                    statuses = listOf(Status.FAILED),
                    types = listOf(TransactionType.CHARGE),
                    creationDateTimeFrom = from,
                    creationDateTimeTo = to,
                )
            )
            .filter { tx -> tx.errorCode == ErrorCode.NOT_ENOUGH_FUNDS.name }
            .sortedBy { tx -> tx.creationDateTime }
}
