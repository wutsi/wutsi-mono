package com.wutsi.blog.comment.dto

data class CountCommentResponse(
    val commentStories: List<CommentCounter> = emptyList(),
)
