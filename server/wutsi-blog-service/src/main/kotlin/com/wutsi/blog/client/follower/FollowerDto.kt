package com.wutsi.blog.client.follower

import java.util.Date

data class FollowerDto(
    val id: Long = -1,
    val userId: Long = -1,
    val followerUserId: Long = -1,
    val followDateTime: Date = Date(),
)
