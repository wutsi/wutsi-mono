package com.wutsi.blog.comment.dto

import java.util.Date

data class Comment(
    val id: Long = -1,
    val userId: Long = -1,
    val storyId: Long = -1,
    val text: String = "",
    val timestamp: Date = Date(),
)
