package com.wutsi.blog.comment.dto

data class SearchCommentRequest(
    val storyIds: List<Long> = emptyList(),
    val userId: Long? = null,
)
