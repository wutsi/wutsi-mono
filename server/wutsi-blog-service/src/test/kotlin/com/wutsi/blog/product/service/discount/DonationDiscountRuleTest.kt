package com.wutsi.blog.product.service.discount

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.service.TransactionService
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.DateUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.util.Date

class DonationDiscountRuleTest {
    private val transactionService = mock<TransactionService>()
    private val userService = mock<UserService>()
    private val rule = DonationDiscountRule(transactionService, userService)

    private val fmt = SimpleDateFormat("yyyy-MM-dd")
    private val user = UserEntity(id = 1)
    private val blog = UserEntity(id = 111, walletId = "111", country = "CM")
    private val store = StoreEntity(userId = blog.id!!)
    private val country = Country.all.find { it.code.equals(blog.country, true) }!!

    @BeforeEach
    fun setUp() {
        doReturn(blog).whenever(userService).findById(blog.id!!)
    }

    @Test
    fun `one week`() {
        // GIVEN
        val tx = createTransaction(country.defaultDonationAmounts[0])
        doReturn(listOf(tx)).whenever(transactionService).search(any())

        // WHEN
        val discount = rule.apply(store, user)

        // THEN
        assertEquals(DiscountType.DONATION, discount?.type)
        assertEquals(100, discount?.percentage)
        assertEquals(fmt.format(DateUtils.addDays(tx.creationDateTime, 7)), fmt.format(discount?.expiryDate))
    }

    @Test
    fun `one month`() {
        // GIVEN
        val tx = createTransaction(country.defaultDonationAmounts[1])
        doReturn(listOf(tx)).whenever(transactionService).search(any())

        // WHEN
        val discount = rule.apply(store, user)

        // THEN
        assertEquals(DiscountType.DONATION, discount?.type)
        assertEquals(100, discount?.percentage)
        assertEquals(fmt.format(DateUtils.addMonths(tx.creationDateTime, 1)), fmt.format(discount?.expiryDate))
    }

    @Test
    fun `one quarter`() {
        // GIVEN
        val tx = createTransaction(country.defaultDonationAmounts[2])
        doReturn(listOf(tx)).whenever(transactionService).search(any())

        // WHEN
        val discount = rule.apply(store, user)

        // THEN
        assertEquals(DiscountType.DONATION, discount?.type)
        assertEquals(100, discount?.percentage)
        assertEquals(fmt.format(DateUtils.addMonths(tx.creationDateTime, 3)), fmt.format(discount?.expiryDate))
    }

    @Test
    fun `two quarters`() {
        // GIVEN
        val tx = createTransaction(country.defaultDonationAmounts[3])
        doReturn(listOf(tx)).whenever(transactionService).search(any())

        // WHEN
        val discount = rule.apply(store, user)

        // THEN
        assertEquals(DiscountType.DONATION, discount?.type)
        assertEquals(100, discount?.percentage)
        assertEquals(fmt.format(DateUtils.addMonths(tx.creationDateTime, 6)), fmt.format(discount?.expiryDate))
    }

    @Test
    fun `no donation`() {
        // GIVEN
        doReturn(emptyList<TransactionEntity>()).whenever(transactionService).search(any())

        // WHEN
        val discount = rule.apply(store, user)

        // THEN
        assertNull(discount)
    }

    @Test
    fun `no wallet`() {
        // GIVEN
        doReturn(blog.copy(walletId = null)).whenever(userService).findById(blog.id!!)

        // WHEN
        val discount = rule.apply(store, user)

        // THEN
        assertNull(discount)
    }

    private fun createTransaction(amount: Long) = TransactionEntity(
        creationDateTime = Date(),
        amount = amount
    )
}
