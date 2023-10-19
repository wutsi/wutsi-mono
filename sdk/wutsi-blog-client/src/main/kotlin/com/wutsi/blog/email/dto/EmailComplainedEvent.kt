package com.wutsi.blog.email.dto

data class EmailComplainedEvent(
    val email: String = "",
    val messageId: String = "",
)
