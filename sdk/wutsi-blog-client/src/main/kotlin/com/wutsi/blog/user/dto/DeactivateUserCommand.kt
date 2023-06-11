package com.wutsi.blog.user.dto

data class DeactivateUserCommand(
    val userId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
