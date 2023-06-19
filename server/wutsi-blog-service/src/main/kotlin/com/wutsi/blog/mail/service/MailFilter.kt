package com.wutsi.blog.mail.service

interface MailFilter {
    fun filter(html: String, context: MailContext): String
}
