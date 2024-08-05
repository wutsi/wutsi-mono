package com.wutsi.blog.account.dto

import jakarta.validation.constraints.NotEmpty

data class LogoutUserCommand(
    @get:NotEmpty val accessToken: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
