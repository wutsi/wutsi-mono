package com.wutsi.blog.product.service

import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.service.discount.DonationDiscountRule
import com.wutsi.blog.product.service.discount.FirstPurchaseDiscountRule
import com.wutsi.blog.product.service.discount.NextPurchaseDiscountRule
import com.wutsi.blog.product.service.discount.SubscriberDiscountRule
import com.wutsi.blog.user.domain.UserEntity
import org.springframework.stereotype.Service

@Service
class DiscountRuleSet(
    private val subscriberRule: SubscriberDiscountRule,
    private val firstPurchaseRule: FirstPurchaseDiscountRule,
    private val nextPurchaseRule: NextPurchaseDiscountRule,
    private val donationRuleSet: DonationDiscountRule,
) {
    private val rules: List<DiscountRule>
        get() = listOf(
            subscriberRule,
            nextPurchaseRule,
            firstPurchaseRule,
            donationRuleSet
        )

    fun findDiscounts(store: StoreEntity, user: UserEntity): List<Discount> =
        rules
            .mapNotNull { it.apply(store, user) }
            .sortedByDescending { it.percentage }
}
