package com.wutsi.blog.mail.service

data class MailContext(
    val blog: Blog,
    val websiteUrl: String,
    val assetUrl: String,
    val template: String,
)
