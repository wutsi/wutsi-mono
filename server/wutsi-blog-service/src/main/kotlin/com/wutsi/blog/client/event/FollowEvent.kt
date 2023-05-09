package com.wutsi.blog.client.event

data class FollowEvent(
    val userId: Long,
    val followerUserId: Long,
)
