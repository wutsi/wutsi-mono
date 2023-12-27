package com.wutsi.blog.product.service.discount

import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.service.DiscountRule
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.payment.core.Status
import org.springframework.stereotype.Service

@Service
class FirstPurchaseDiscountRule(
    private val transactionDao: TransactionRepository
) : DiscountRule {
    override fun qualify(store: StoreEntity, user: UserEntity): Boolean =
        if (store.firstPurchaseDiscount > 0) {
            transactionDao.findByStoreAndUserAndTypeAndStatusOrderByCreationDateTimeDesc(store, user, TransactionType.CHARGE, Status.SUCCESSFUL).isEmpty()
        } else {
            false
        }
}
