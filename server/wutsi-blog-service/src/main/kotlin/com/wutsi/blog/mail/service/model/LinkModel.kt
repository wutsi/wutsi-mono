package com.wutsi.blog.mail.service.model

data class LinkModel(
    val title: String = "",
    val url: String = "",
    val thumbnailUrl: String? = null,
    val imageUrl: String? = null,
    val summary: String? = null,
    val description: String? = null,
    val shortDescription: String? = null,
    val author: String? = null,
    val authorPictureUrl: String? = null,
    val authorUrl: String? = null,
    val score: Double? = null,
)
