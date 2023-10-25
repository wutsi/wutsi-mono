package com.wutsi.blog.mail.service.ses

data class SESMail(
    val messageId: String = "",
    val timestamp: String = "",
    val source: String = "",
    val sendingAccount: String = "",
    val headerTruncated: Boolean = false,
)
