package com.wutsi.blog.product.service.discount

import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.service.DiscountRule
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.user.domain.UserEntity
import org.springframework.stereotype.Service

@Service
class SubscriberDiscountRule(
    private val subscriptionDao: SubscriptionRepository
) : DiscountRule {
    override fun qualify(store: StoreEntity, user: UserEntity): Boolean =
        if (store.subscriberDiscount > 0 && store.userId != user.id) {
            subscriptionDao.findByUserIdAndSubscriberId(store.userId, user.id ?: -1) == null
        } else {
            false
        }
}
