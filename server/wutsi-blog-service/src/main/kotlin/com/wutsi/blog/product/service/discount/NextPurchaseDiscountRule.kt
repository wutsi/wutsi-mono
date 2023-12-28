package com.wutsi.blog.product.service.discount

import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.service.DiscountRule
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.payment.core.Status
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.math.abs

@Service
class NextPurchaseDiscountRule(
    private val transactionDao: TransactionRepository
) : DiscountRule {
    override fun apply(store: StoreEntity, user: UserEntity): Discount? {
        if (store.nextPurchaseDiscount > 0 && store.nextPurchaseDiscountDays > 0) {
            val txs = transactionDao.findByStoreAndUserAndTypeAndStatusOrderByCreationDateTimeDesc(
                store,
                user,
                TransactionType.CHARGE,
                Status.SUCCESSFUL
            )
            if (txs.isNotEmpty()) {
                if (daysBetween(txs[0].creationDateTime, Date()) <= store.nextPurchaseDiscountDays) {
                    return Discount(
                        type = DiscountType.NEXT_PURCHASE,
                        percentage = store.nextPurchaseDiscount,
                        expiryDate = DateUtils.beginingOfTheDay(
                            DateUtils.addDays(
                                Date(),
                                store.nextPurchaseDiscountDays
                            )
                        )
                    )
                }
            }
        }
        return null
    }

    private fun daysBetween(date1: Date, date2: Date): Long =
        abs((date1.time - date2.time) / (86400L * 1000L))
}
