package com.wutsi.blog.user.dto

data class UserAttributeUpdatedEventPayload(
    val name: String = "",
    val value: String? = null,
)
