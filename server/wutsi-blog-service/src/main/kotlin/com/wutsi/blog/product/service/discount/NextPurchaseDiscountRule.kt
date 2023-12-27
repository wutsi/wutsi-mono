package com.wutsi.blog.product.service.discount

import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.service.DiscountRule
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.payment.core.Status
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.math.abs

@Service
class NextPurchaseDiscountRule(
    private val transactionDao: TransactionRepository
) : DiscountRule {
    override fun qualify(store: StoreEntity, user: UserEntity): Boolean =
        if (store.nextPurchaseDiscount > 0 && store.nextPurchaseDiscountDays > 0) {
            val txs = transactionDao.findByStoreAndUserAndTypeAndStatusOrderByCreationDateTimeDesc(
                store,
                user,
                TransactionType.CHARGE,
                Status.SUCCESSFUL
            )
            if (txs.isEmpty()) {
                false
            } else {
                daysBetween(txs[0].creationDateTime, Date()) <= store.nextPurchaseDiscountDays
            }
        } else {
            false
        }

    private fun daysBetween(date1: Date, date2: Date): Long =
        abs((date1.time - date2.time) / (86400L * 1000L))
}
