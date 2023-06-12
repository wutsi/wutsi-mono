package com.wutsi.blog.mail.service

interface MailFilter {
    fun filter(body: String, context: MailContext): String
}
