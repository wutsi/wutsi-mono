package com.wutsi.blog.client.follower

data class SearchFollowerRequest(
    val userId: Long? = null,
    val followerUserId: Long? = null,
    val limit: Int = 20,
    val offset: Int = 0,
)
