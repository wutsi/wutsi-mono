package com.wutsi.blog.product.service.discount

import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.service.DiscountRule
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.transaction.service.TransactionService
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.service.UserService
import com.wutsi.platform.payment.core.Status
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Service
import java.util.Date

@Service
class DonationDiscountRule(
    private val transactionService: TransactionService,
    private val userService: UserService,
) : DiscountRule {
    override fun apply(store: StoreEntity, user: UserEntity): Discount? {
        val blog = userService.findById(store.userId)
        val tx = findTransaction(blog, user)
        return tx?.let {
            val expiryDate = computeExpiryDate(blog, tx)
            Discount(
                type = DiscountType.DONATION,
                percentage = 100,
                expiryDate = expiryDate
            )
        }
    }

    private fun findTransaction(blog: UserEntity, user: UserEntity): TransactionEntity? {
        if (blog.walletId == null) {
            return null
        }

        val now = Date()
        return transactionService.search(
            SearchTransactionRequest(
                types = listOf(TransactionType.DONATION),
                statuses = listOf(Status.SUCCESSFUL),
                walletId = blog.walletId,
                userId = user.id,
                creationDateTimeFrom = DateUtils.addYears(now, -1),
                creationDateTimeTo = now,
                limit = 1,
            )
        ).firstOrNull()
    }

    private fun computeExpiryDate(blog: UserEntity, tx: TransactionEntity): Date? {
        val startDate = tx.creationDateTime
        val country = Country.all.find { country -> country.code.equals(blog.country, true) } ?: return null
        return if (tx.amount < country.defaultDonationAmounts[1]) {
            DateUtils.addDays(startDate, 7)
        } else if (tx.amount < country.defaultDonationAmounts[2]) {
            DateUtils.addMonths(startDate, 1)
        } else if (tx.amount < country.defaultDonationAmounts[3]) {
            DateUtils.addMonths(startDate, 3)
        } else {
            DateUtils.addMonths(startDate, 6)
        }
    }
}
