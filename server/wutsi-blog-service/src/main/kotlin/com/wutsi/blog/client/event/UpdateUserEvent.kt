package com.wutsi.blog.client.event

data class UpdateUserEvent(
    val userId: Long,
    val name: String,
    val value: String?,
)
