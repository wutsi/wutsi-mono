package com.wutsi.blog.comment.dto

data class SearchCommentRequest(
    val storyId: Long = -1,
    val limit: Int = 20,
    val offset: Int = 0,
)
