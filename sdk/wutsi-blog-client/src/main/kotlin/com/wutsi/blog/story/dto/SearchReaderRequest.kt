package com.wutsi.blog.story.dto

data class SearchReaderRequest(
    val storyId: Long? = null,
    val subscribedToUserId: Long? = null,
    val limit: Int = 20,
    val offset: Int = 0,
)
