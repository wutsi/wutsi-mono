package com.wutsi.blog.client.event

data class UnfollowEvent(
    val userId: Long,
    val followerUserId: Long,
)
