package com.wutsi.blog.subscription.dto

data class CountSubscriptionResponse(
    val counters: List<SubscriptionCounter> = emptyList(),
)
