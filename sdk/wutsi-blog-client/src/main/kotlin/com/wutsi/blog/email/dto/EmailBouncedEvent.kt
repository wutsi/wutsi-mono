package com.wutsi.blog.email.dto

data class EmailBouncedEvent(
    val email: String = "",
    val messageId: String = "",
)
