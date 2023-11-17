package com.wutsi.blog.account.dto

data class Link(
    val email: String = "",
    val redirectUrl: String? = null,
    val referer: String? = null,
    val storyId: Long? = null,
    val language: String = "",
)
