package com.wutsi.blog.subscription.dto

data class SubscriptionCounter(
    val userId: Long = -1,
    val count: Long = 0,
    val subscribed: Boolean = false,
)
