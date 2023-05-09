package com.wutsi.blog.client.extractor

import java.util.Date

data class WebPageDto(
    val url: String = "",
    val title: String = "",
    val tags: List<String> = emptyList(),
    val content: String = "",
    val publishedDate: Date? = null,
    val siteName: String = "",
    val image: String? = null,
)
