package com.wutsi.blog.transaction.job

import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.event.EventType.SUBMIT_CASHOUT_COMMAND
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.transaction.dto.SubmitCashoutCommand
import com.wutsi.blog.transaction.service.TransactionService
import com.wutsi.blog.transaction.service.WalletService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class TransactionCashoutJob(
    private val service: WalletService,
    private val transactionService: TransactionService,
    private val eventStream: EventStream,
    private val logger: KVLogger,

    lockManager: CronLockManager,
    registry: CronJobRegistry,

    @Value("\${wutsi.application.transaction.cashout.currency}") private val currency: String,
) : AbstractCronJob(lockManager, registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TransactionCashoutJob::class.java)
    }

    override fun getJobName() = "transaction-cashout"

    @Scheduled(cron = "\${wutsi.crontab.transaction-cashout}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val wallets = service.findWalletToCashout(Date())

        var cashout = 0L
        var errors = 0L
        wallets.forEach { wallet ->
            try {
                val amount = transactionService.computeCashoutAmount(wallet)
                val minCashoutAmount = service.getMinCashoutAmount(wallet)
                if (amount >= minCashoutAmount && wallet.currency == currency) {
                    eventStream.enqueue(
                        type = SUBMIT_CASHOUT_COMMAND,
                        payload = SubmitCashoutCommand(
                            walletId = wallet.id!!,
                            amount = amount,
                            currency = wallet.currency,
                            idempotencyKey = UUID.randomUUID().toString(),
                        ),
                    )
                    cashout++
                }
            } catch (ex: Exception) {
                errors++
                LOGGER.info("Unable to cashout Wallet#${wallet.id}", ex)
            }
        }
        logger.add("cashout_count", cashout)
        logger.add("cashout_errors", errors)
        return cashout
    }

    fun getMinCashoutAmount(wallet: WalletEntity): Long =
        Country.all.find { it.code == wallet.country }?.minCashoutAmount ?: 0L
}
