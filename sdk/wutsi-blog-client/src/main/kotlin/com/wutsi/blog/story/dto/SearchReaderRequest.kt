package com.wutsi.blog.story.dto

data class SearchReaderRequest(
    val storyId: Long? = null,
    val userId: Long? = null,
    val limit: Int = 20,
    val offset: Int = 0,
    val subscribersOnly: Boolean = true,
)
