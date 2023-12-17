package com.wutsi.blog.mail.service

import com.wutsi.blog.mail.service.model.BlogModel

data class MailContext(
    val blog: BlogModel,
    val websiteUrl: String,
    val assetUrl: String,
    val template: String,
    val storyId: Long? = null,
)
