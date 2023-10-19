package com.wutsi.blog.mail.dto

data class EmailBouncedEvent(
    val email: String = "",
    val messageId: String = "",
)
