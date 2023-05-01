package com.wutsi.blog.sdk

interface NewsletterApi {
    fun unsubscribe(email: String)
    fun unsubscribe(userId: Long, email: String)
}
