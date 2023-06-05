package com.wutsi.blog.comment.dto

data class CountCommentResponse(
    val counters: List<CommentCounter> = emptyList(),
)
