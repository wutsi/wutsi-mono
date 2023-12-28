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
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.payment.core.Status
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.Date

class NextPurchaseDiscountRuleTest {
    private val dao = mock<TransactionRepository>()

    private val rule = NextPurchaseDiscountRule(dao)

    @Test
    fun `rule reject if no percent`() {
        val user = UserEntity(id = 1)
        val store = StoreEntity(nextPurchaseDiscount = 0, nextPurchaseDiscountDays = 14)

        assertNull(rule.apply(store, user))
    }

    @Test
    fun `rule reject if no discount days`() {
        val user = UserEntity(id = 1)
        val store = StoreEntity(nextPurchaseDiscount = 5, nextPurchaseDiscountDays = 0)

        assertNull(rule.apply(store, user))
    }

    @Test
    fun `rule reject if no transactions`() {
        val user = UserEntity(id = 1)
        val store = StoreEntity(nextPurchaseDiscount = 5, nextPurchaseDiscountDays = 15)
        doReturn(emptyList<TransactionEntity>())
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
    fun `rule reject if transactions within days`() {
        val user = UserEntity(id = 1)
        val store = StoreEntity(nextPurchaseDiscount = 5, nextPurchaseDiscountDays = 15)
        doReturn(
            listOf(
                TransactionEntity(creationDateTime = DateUtils.addDays(Date(), -30)),
                TransactionEntity(creationDateTime = DateUtils.addDays(Date(), -60))
            )
        )
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
    fun `rule accept if transactions within days`() {
        val user = UserEntity(id = 1)
        val store = StoreEntity(nextPurchaseDiscount = 5, nextPurchaseDiscountDays = 15)
        doReturn(
            listOf(
                TransactionEntity(creationDateTime = DateUtils.addDays(Date(), -10)),
                TransactionEntity(creationDateTime = DateUtils.addDays(Date(), -11)),
                TransactionEntity(creationDateTime = DateUtils.addDays(Date(), -20)),
            )
        )
            .whenever(dao)
            .findByStoreAndUserAndTypeAndStatusOrderByCreationDateTimeDesc(
                store,
                user,
                TransactionType.CHARGE,
                Status.SUCCESSFUL
            )

        val discount = rule.apply(store, user)
        assertEquals(DiscountType.NEXT_PURCHASE, discount?.type)
        assertEquals(store.nextPurchaseDiscount, discount?.percentage)
        assertEquals(
            DateUtils.beginingOfTheDay(DateUtils.addDays(Date(), store.nextPurchaseDiscountDays)),
            discount?.expiryDate
        )
    }

    @Test
    fun `rule accept if transactions at days`() {
        val user = UserEntity(id = 1)
        val store = StoreEntity(nextPurchaseDiscount = 5, nextPurchaseDiscountDays = 15)
        doReturn(
            listOf(
                TransactionEntity(creationDateTime = DateUtils.addDays(Date(), -store.nextPurchaseDiscount))
            )
        )
            .whenever(dao)
            .findByStoreAndUserAndTypeAndStatusOrderByCreationDateTimeDesc(
                store,
                user,
                TransactionType.CHARGE,
                Status.SUCCESSFUL
            )

        val discount = rule.apply(store, user)
        assertEquals(DiscountType.NEXT_PURCHASE, discount?.type)
        assertEquals(store.nextPurchaseDiscount, discount?.percentage)
        assertEquals(
            DateUtils.beginingOfTheDay(DateUtils.addDays(Date(), store.nextPurchaseDiscountDays)),
            discount?.expiryDate
        )
    }
}
