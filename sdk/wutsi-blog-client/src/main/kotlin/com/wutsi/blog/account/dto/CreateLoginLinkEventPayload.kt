package com.wutsi.blog.account.dto

data class CreateLoginLinkEventPayload(
    val email: String = "",
    val redirectUrl: String? = null,
)
