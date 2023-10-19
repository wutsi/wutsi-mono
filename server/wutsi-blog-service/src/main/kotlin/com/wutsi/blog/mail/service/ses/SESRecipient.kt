package com.wutsi.blog.mail.service.ses

data class SESRecipient(
    val emailAddress: String = "",
    val action: String = "",
    val status: String = "",
    val diagnosticCode: String = "",
)
