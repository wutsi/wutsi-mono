package com.wutsi.blog.subscription.dto

import java.util.Date

data class Subscription(
    val userId: Long = -1,
    val subscriberId: Long = -1,
    val subscriptionDateTime: Date = Date(),
)
