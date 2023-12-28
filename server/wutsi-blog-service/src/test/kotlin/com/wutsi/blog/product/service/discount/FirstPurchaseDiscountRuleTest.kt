package com.wutsi.blog.product.service.discount

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.payment.core.Status
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class FirstPurchaseDiscountRuleTest {
    private val dao = mock<TransactionRepository>()

    private val rule = FirstPurchaseDiscountRule(dao)

    @Test
    fun `rule reject if no percent`() {
        val user = UserEntity(id = 1)
        val store = StoreEntity(firstPurchaseDiscount = 0)

        assertNull(rule.apply(store, user))
    }

    @Test
    fun `rule reject if user has already a purchase`() {
        val user = UserEntity(id = 1)
        val store = StoreEntity(firstPurchaseDiscount = 0)
        doReturn(listOf(TransactionEntity()))
            .whenever(dao)
            .findByStoreAndUserAndTypeAndStatusOrderByCreationDateTimeDesc(
                store,
                user,
                TransactionType.CHARGE,
                Status.SUCCESSFUL
            )

        assertNull(rule.apply(store, user))
    }

    @Test
    fun `rule accept if no purchase`() {
        val user = UserEntity(id = 1)
        val store = StoreEntity(firstPurchaseDiscount = 5)
        doReturn(emptyList<TransactionEntity>())
            .whenever(dao)
            .findByStoreAndUserAndTypeAndStatusOrderByCreationDateTimeDesc(
                store,
                user,
                TransactionType.CHARGE,
                Status.SUCCESSFUL
            )

        val discount = rule.apply(store, user)
        assertEquals(DiscountType.FIRST_PURCHASE, discount?.type)
        assertEquals(store.firstPurchaseDiscount, discount?.percentage)
        assertNull(discount?.expiryDate)
    }
}
