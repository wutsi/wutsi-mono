package com.wutsi.blog.subscription.dto

data class SearchSubscriptionRequest(
    val userIds: List<Long> = emptyList(),
    val subscriberId: Long? = null,
    val limit: Int = 20,
    val offset: Int = 0,
)
