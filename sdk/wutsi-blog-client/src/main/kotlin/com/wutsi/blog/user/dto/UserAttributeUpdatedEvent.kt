package com.wutsi.blog.user.dto

data class UserAttributeUpdatedEvent(
    val name: String = "",
    val value: String? = null,
)
