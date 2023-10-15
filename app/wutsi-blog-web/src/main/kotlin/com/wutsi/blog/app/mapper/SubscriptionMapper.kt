package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.SubscriptionModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.Moment
import com.wutsi.blog.subscription.dto.Subscription
import org.springframework.stereotype.Service

@Service
class SubscriptionMapper(private val moment: Moment) {
    fun toSubscriptionModel(obj: Subscription, subscriber: UserModel? = null) = SubscriptionModel(
        userId = obj.userId,
        subscriberId = obj.subscriberId,
        subscriptionDateTime = obj.subscriptionDateTime,
        subscriptionDateTimeText = moment.format(obj.subscriptionDateTime),
        subscriber = subscriber ?: UserModel(obj.subscriberId),
    )
}
