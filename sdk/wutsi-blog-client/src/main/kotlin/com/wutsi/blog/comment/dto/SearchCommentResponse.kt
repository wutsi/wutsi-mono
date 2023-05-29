package com.wutsi.blog.comment.dto

data class SearchCommentResponse(
    val comments: List<Comment> = emptyList(),
)
