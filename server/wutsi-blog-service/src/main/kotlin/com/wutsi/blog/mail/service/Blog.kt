package com.wutsi.blog.mail.service

data class Blog(
    val name: String,
    val fullName: String,
    val language: String,
    val logoUrl: String?,
    val facebookUrl: String? = null,
    val twitterUrl: String? = null,
    val youtubeUrl: String? = null,
    val linkedInUrl: String? = null,
    val whatsappUrl: String? = null,
    val subscribedUrl: String? = null,
    val unsubscribedUrl: String? = null,
)
