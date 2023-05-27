package com.wutsi.blog.comment.dto

data class CommentStory(
    val storyId: Long = -1,
    val count: Long = 0,
    val commented: Boolean = false,
)
