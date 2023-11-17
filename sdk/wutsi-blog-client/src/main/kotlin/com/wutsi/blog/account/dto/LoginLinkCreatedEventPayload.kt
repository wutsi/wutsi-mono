package com.wutsi.blog.account.dto

data class LoginLinkCreatedEventPayload(
    val email: String = "",
    val redirectUrl: String? = null,
    val referer: String? = null,
    val storyId: Long? = null,
    val language: String = "",
)
