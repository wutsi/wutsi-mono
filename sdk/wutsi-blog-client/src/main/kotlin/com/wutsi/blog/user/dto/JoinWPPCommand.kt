package com.wutsi.blog.user.dto

data class JoinWPPCommand(
    val userId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
