package com.wutsi.blog.product.service.discount

import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.service.DiscountRule
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.user.domain.UserEntity
import org.springframework.stereotype.Service

@Service
class SubscriberDiscountRule(
    private val subscriptionDao: SubscriptionRepository
) : DiscountRule {
    override fun apply(store: StoreEntity, user: UserEntity): Discount? {
        if (store.subscriberDiscount > 0) {
            if (subscriptionDao.findByUserIdAndSubscriberId(store.userId, user.id ?: -1) != null) {
                return Discount(
                    type = DiscountType.SUBSCRIBER,
                    percentage = store.subscriberDiscount
                )
            }
        }
        return null
    }
}
