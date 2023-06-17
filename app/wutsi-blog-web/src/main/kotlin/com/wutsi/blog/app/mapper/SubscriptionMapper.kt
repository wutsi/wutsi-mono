package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.SubscriptionModel
import com.wutsi.blog.subscription.dto.Subscription
import org.springframework.stereotype.Service

@Service
class SubscriptionMapper {
    fun toSubscriptionModel(obj: Subscription) = SubscriptionModel(
        userId = obj.userId,
        subscriberId = obj.subscriberId,
    )
}
