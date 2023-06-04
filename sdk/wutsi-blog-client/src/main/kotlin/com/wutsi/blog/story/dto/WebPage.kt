package com.wutsi.blog.story.dto

import java.util.Date

data class WebPage(
    val url: String = "",
    val title: String = "",
    val tags: List<String> = emptyList(),
    val content: String = "",
    val publishedDate: Date? = null,
    val siteName: String = "",
    val image: String? = null,
)
