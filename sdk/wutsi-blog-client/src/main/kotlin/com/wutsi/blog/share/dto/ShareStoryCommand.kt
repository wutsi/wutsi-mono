package com.wutsi.blog.share.dto

data class ShareStoryCommand(
    val storyId: Long = -1,
    val userId: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
