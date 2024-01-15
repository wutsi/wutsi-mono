package com.wutsi.blog.product.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.service.discount.DonationDiscountRule
import com.wutsi.blog.product.service.discount.FirstPurchaseDiscountRule
import com.wutsi.blog.product.service.discount.NextPurchaseDiscountRule
import com.wutsi.blog.product.service.discount.SubscriberDiscountRule
import com.wutsi.blog.user.domain.UserEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DiscountRuleSetTest {
    private val subscriberRule: SubscriberDiscountRule = mock()
    private val nextPurchaseRule: NextPurchaseDiscountRule = mock()
    private val firstPurchaseRule: FirstPurchaseDiscountRule = mock()
    private val donationDiscountRule: DonationDiscountRule = mock()
    private val ruleSet = DiscountRuleSet(
        subscriberRule,
        firstPurchaseRule,
        nextPurchaseRule,
        donationDiscountRule,
    )

    @Test
    fun findDiscounts() {
        // GIVEN
        val discount1: Discount? = null
        val discount2 = Discount(percentage = 5)
        val discount3 = Discount(percentage = 15)

        doReturn(discount1).whenever(subscriberRule).apply(any(), any())
        doReturn(discount2).whenever(nextPurchaseRule).apply(any(), any())
        doReturn(discount3).whenever(firstPurchaseRule).apply(any(), any())
        doReturn(null).whenever(donationDiscountRule).apply(any(), any())

        // WHEN
        val result = ruleSet.findDiscounts(StoreEntity(), UserEntity())

        // THEN
        assertEquals(listOf(discount3, discount2), result)
    }
}
