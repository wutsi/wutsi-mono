package com.wutsi.blog.story.dto

data class Reader(
    val id: Long = -1,
    val userId: Long = -1,
    val storyId: Long = -1,
    var commented: Boolean = false,
    var liked: Boolean = false,
    var subscribed: Boolean = false,
)
