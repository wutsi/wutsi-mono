package com.wutsi.blog.user.dto

data class ActivateUserCommand(
    val userId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
