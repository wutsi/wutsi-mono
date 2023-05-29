package com.wutsi.blog.subscription.dto

data class CountSubscriptionRequest(
    val userIds: List<Long> = emptyList(),
    val subscriberId: Long? = null,
)
