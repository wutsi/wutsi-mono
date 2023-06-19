package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.MailFilter
import org.jsoup.Jsoup

class ButtonFilter : MailFilter {
    override fun filter(html: String, context: MailContext): String {
        val doc = Jsoup.parse(html)
        doc.select(".story-content .button").forEach {
            it.addClass("margin-top")
            it.addClass("margin-bottom")
            it.addClass("text-center")
        }
        doc.select(".story-content .button a").forEach {
            it.addClass("btn-primary")
        }
        return doc.html()
    }
}
