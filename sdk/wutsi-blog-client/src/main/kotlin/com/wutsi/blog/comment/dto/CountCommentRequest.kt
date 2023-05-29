package com.wutsi.blog.comment.dto

data class CountCommentRequest(
    val storyIds: List<Long> = emptyList(),
    val userId: Long? = null,
)
