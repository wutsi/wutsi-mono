package com.wutsi.mail

interface MailFilter {
    fun filter(body: String, context: MailContext): String
}
