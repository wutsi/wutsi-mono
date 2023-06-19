package com.wutsi.blog.account.dto

import javax.validation.constraints.NotEmpty

data class LoginUserAsCommand(
    @get:NotEmpty val accessToken: String = "",
    @get:NotEmpty val userName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
