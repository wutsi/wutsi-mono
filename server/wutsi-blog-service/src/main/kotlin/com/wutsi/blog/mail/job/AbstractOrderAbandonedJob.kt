package com.wutsi.blog.mail.job

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
import java.time.LocalDate

abstract class AbstractOrderAbandonedJob(
    private val transactionService: TransactionService,
    private val logger: KVLogger,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    protected abstract fun send(tx: TransactionEntity): Boolean

    protected abstract fun fromDate(): LocalDate

    protected abstract fun toDate(): LocalDate?

    override fun doRun(): Long {
        val to = toDate()
        val from = fromDate()
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

    private fun isAbandoned(tx: TransactionEntity): Boolean {
        val txs = transactionService.search(
            SearchTransactionRequest(
                types = listOf(tx.type),
                statuses = listOf(Status.SUCCESSFUL, Status.PENDING),
                productIds = tx.product?.id?.let { productId -> listOf(productId) } ?: emptyList(),
                creationDateTimeFrom = tx.creationDateTime,
                userId = tx.user?.id,
                email = tx.email,
                storeId = tx.store?.id,
                walletId = tx.wallet.id,
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

    private fun findTransactions(from: LocalDate, to: LocalDate?): List<TransactionEntity> =
        transactionService.search(
            SearchTransactionRequest(
                statuses = listOf(Status.FAILED),
                types = listOf(TransactionType.DONATION, TransactionType.CHARGE),
                creationDateTimeFrom = DateUtils.toDate(from),
                creationDateTimeTo = to?.let { DateUtils.toDate(to) },
            )
        ).sortedBy { it.creationDateTime }
}
