package com.wutsi.blog.client.comment

import java.util.Date

data class SearchCommentRequest(
    val storyIds: List<Long> = emptyList(),
    val authorId: Long? = null,
    val since: Date? = null,
    val limit: Int = 20,
    val offset: Int = -1,
)
