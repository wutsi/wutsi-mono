package com.wutsi.blog.account.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class CreateLoginLinkCommand(
    @get:Email @get:NotBlank val email: String = "",
    @get:NotBlank val language: String = "",
    val redirectUrl: String? = null,
    val referer: String? = null,
    val storyId: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
)
