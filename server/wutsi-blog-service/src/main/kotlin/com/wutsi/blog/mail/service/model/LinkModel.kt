package com.wutsi.blog.mail.service.model

data class LinkModel(
    val title: String = "",
    val url: String = "",
    val thumbnailUrl: String? = null,
    val summary: String? = null,
)
