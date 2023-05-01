package com.wutsi.blog.client.comment

data class SearchCommentResponse(
    val comments: List<CommentDto> = emptyList(),
)
