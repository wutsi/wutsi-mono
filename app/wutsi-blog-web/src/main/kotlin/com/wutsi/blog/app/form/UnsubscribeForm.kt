package com.wutsi.blog.app.form

data class UnsubscribeForm(
    val userId: Long = -1,
    val email: String? = null,
    val subscriberId: Long = -1,
)
