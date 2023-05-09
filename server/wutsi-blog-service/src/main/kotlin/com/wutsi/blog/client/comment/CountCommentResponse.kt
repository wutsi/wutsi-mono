package com.wutsi.blog.client.comment

data class CountCommentResponse(
    val counts: List<CommentCountDto> = emptyList(),
)
