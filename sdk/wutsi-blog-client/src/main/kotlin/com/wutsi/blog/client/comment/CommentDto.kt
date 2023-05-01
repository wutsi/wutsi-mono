package com.wutsi.blog.client.comment

import java.util.Date

data class CommentDto(
    val id: Long = -1,
    val userId: Long = -1,
    val storyId: Long = -1,
    val text: String = "",
    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
)
