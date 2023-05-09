package com.wutsi.blog.client.like

import java.util.Date

data class LikeDto(
    val id: Long = -1,
    val storyId: Long = -1,
    val userId: Long? = null,
    val deviceId: String? = null,
    val likeDateTime: Date = Date(),
)
