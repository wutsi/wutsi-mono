package com.wutsi.blog.mail.service

data class Blog(
    val name: String,
    val fullName: String,
    val logoUrl: String?,
    val facebookUrl: String? = null,
    val twitterUrl: String? = null,
    val youtubeUrl: String? = null,
    val linkedInUrl: String? = null,
)
