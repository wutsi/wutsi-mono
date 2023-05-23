package com.wutsi.blog.app.component.comment.model

data class CreateCommentForm(
    val storyId: Long = -1,
    val text: String = ""
)
