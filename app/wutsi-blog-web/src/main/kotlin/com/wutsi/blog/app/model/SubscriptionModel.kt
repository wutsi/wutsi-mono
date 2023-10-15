package com.wutsi.blog.app.model

import java.util.Date

data class SubscriptionModel(
    val userId: Long = -1,
    val subscriberId: Long = -1,
    val subscriber: UserModel = UserModel(),
    val subscriptionDateTime: Date = Date(),
    val subscriptionDateTimeText: String = "",
)
