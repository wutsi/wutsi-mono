package com.wutsi.blog.client.view

import java.util.Date

data class SearchViewRequest(
    val storyIds: List<Long> = emptyList(),
    val userId: Long? = null,
    val deviceId: String? = null,
    val viewStartDate: Date? = null,
    val viewEndDate: Date? = null,
    val limit: Int = 100,
    val offset: Int = 0,
)
