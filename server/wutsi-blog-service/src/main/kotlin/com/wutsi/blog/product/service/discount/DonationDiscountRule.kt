package com.wutsi.blog.product.service.discount

import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.service.DiscountRule
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dao.WalletRepository
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.payment.core.Status
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.jvm.optionals.getOrNull

@Service
class DonationDiscountRule(
    private val transactionDao: TransactionRepository,
    private val walletDao: WalletRepository,
    private val userDao: UserRepository,
) : DiscountRule {
    override fun apply(store: StoreEntity, user: UserEntity): Discount? {
        if (!store.enableDonationDiscount) {
            return null
        }

        val discount = doApply(store, user)
        return if (discount?.expiryDate?.before(Date()) == true) {
            null
        } else {
            discount
        }
    }

    fun doApply(store: StoreEntity, user: UserEntity): Discount? {
        val blog = userDao.findById(store.userId).getOrNull() ?: return null
        val tx = findTransaction(blog, user)
        return tx?.let {
            val expiryDate = computeExpiryDate(blog, tx)
            expiryDate?.let {
                Discount(
                    type = DiscountType.DONATION,
                    percentage = 100,
                    expiryDate = expiryDate
                )
            }
        }
    }

    private fun findTransaction(blog: UserEntity, user: UserEntity): TransactionEntity? {
        val wallet: WalletEntity = blog.walletId?.let { walletId ->
            walletDao.findById(walletId).getOrNull()
        } ?: return null
        return transactionDao.findByWalletAndUserAndTypeAndStatusOrderByCreationDateTimeDesc(
            wallet,
            user,
            TransactionType.DONATION,
            Status.SUCCESSFUL
        ).firstOrNull()
    }

    fun computeExpiryDate(blog: UserEntity, tx: TransactionEntity): Date? {
        val startDate = tx.creationDateTime
        val country = Country.all.find { country -> country.code.equals(blog.country, true) } ?: return null
        return if (tx.amount <= 0.0) {
            null
        } else if (tx.amount < country.defaultDonationAmounts[1]) {
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
