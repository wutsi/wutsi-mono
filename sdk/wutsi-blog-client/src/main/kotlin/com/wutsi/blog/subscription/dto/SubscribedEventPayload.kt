package com.wutsi.blog.subscription.dto

data class SubscribedEventPayload(
    val email: String? = null,
    val storyId: Long? = null,
)
