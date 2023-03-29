package com.wutsi.checkout.manager.job

import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.access.dto.SearchOrderRequest
import com.wutsi.checkout.manager.delegate.UpdateOrderStatusDelegate
import com.wutsi.checkout.manager.dto.UpdateOrderStatusRequest
import com.wutsi.enums.OrderStatus
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.DefaultKVLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class ExpireOrderJob(
    private val checkoutAccessApi: CheckoutAccessApi,
    private val delegate: UpdateOrderStatusDelegate,
    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    override fun getJobName() = "expire-order-job"

    @Scheduled(cron = "\${wutsi.application.jobs.pending-transaction.cron}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        var count = 0L
        val limit = 100
        var offset = 0
        val now = OffsetDateTime.now()
        while (true) {
            val orders = checkoutAccessApi.searchOrder(
                request = SearchOrderRequest(
                    limit = limit,
                    offset = offset++,
                    expiresTo = now,
                    status = listOf(
                        OrderStatus.PENDING.name,
                        OrderStatus.UNKNOWN.name,
                    ),
                ),
            ).orders
            orders.forEach {
                if (expire(it.id)) {
                    count++
                }
            }

            if (orders.size < limit) {
                break
            }
        }
        return count
    }

    private fun expire(orderId: String): Boolean {
        val logger = DefaultKVLogger()
        logger.add("job", getJobName())
        logger.add("order_id", orderId)
        try {
            delegate.invoke(
                request = UpdateOrderStatusRequest(orderId, OrderStatus.EXPIRED.name),
                accountId = null,
            )
            return true
        } finally {
            logger.log()
        }
    }
}
