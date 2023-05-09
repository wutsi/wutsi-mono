package com.wutsi.blog.client.like

import java.util.Date

data class SearchLikeRequest(
    val storyIds: List<Long> = emptyList(),
    val userId: Long? = null,
    val deviceId: String? = null,
    val authorId: Long? = null,
    val since: Date? = null,
    val limit: Int = 20,
    val offset: Int = 0,
)
