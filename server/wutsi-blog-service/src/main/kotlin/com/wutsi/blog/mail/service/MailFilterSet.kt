package com.wutsi.blog.mail.service

class MailFilterSet(private val filters: List<MailFilter>) : MailFilter {
    override fun filter(body: String, context: MailContext): String {
        var result = body
        filters.forEach {
            result = it.filter(result, context)
        }
        return result
    }
}
