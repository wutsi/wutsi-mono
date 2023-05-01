package com.wutsi.blog.client.follower

import javax.validation.constraints.NotNull

data class CreateFollowerRequest(
    @get:NotNull val userId: Long? = null,
    @get:NotNull val followerUserId: Long? = null,
)
