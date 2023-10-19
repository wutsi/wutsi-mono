package com.wutsi.blog.mail.dto

data class EmailComplainedEvent(
    val email: String = "",
    val messageId: String = "",
)
