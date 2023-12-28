package com.wutsi.blog.product.service.discount

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.subscription.domain.SubscriptionEntity
import com.wutsi.blog.user.domain.UserEntity
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test
import kotlin.test.assertNull

class SubscriberDiscountRuleTest {
    private val subscriptionDao = mock<SubscriptionRepository>()

    private val rule = SubscriberDiscountRule(subscriptionDao)

    @Test
    fun `rule reject if no percent`() {
        val user = UserEntity(id = 1)
        val store = StoreEntity(subscriberDiscount = 0)

        assertNull(rule.apply(store, user))
    }

    @Test
    fun `rule accept if already subscribed`() {
        val user = UserEntity(id = 1)
        val store = StoreEntity(subscriberDiscount = 5)
        doReturn(SubscriptionEntity()).whenever(subscriptionDao).findByUserIdAndSubscriberId(any(), any())

        val discount = rule.apply(store, user)
        assertEquals(DiscountType.SUBSCRIBER, discount?.type)
        assertEquals(store.subscriberDiscount, discount?.percentage)
        assertNull(discount?.expiryDate)
    }

    @Test
    fun `rule reject if not subscribed`() {
        val user = UserEntity(id = 1)
        val store = StoreEntity(subscriberDiscount = 5)
        doReturn(null).whenever(subscriptionDao).findByUserIdAndSubscriberId(any(), any())

        assertNull(rule.apply(store, user))
    }
}
