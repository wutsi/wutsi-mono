package com.wutsi.blog.subscription.dto

data class SearchSubscriptionResponse(
    val subscriptions: List<Subscription> = emptyList(),
)
