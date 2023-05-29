package com.wutsi.blog.app.form

data class CreateCommentForm(
    val storyId: Long = -1,
    val text: String = "",
)
