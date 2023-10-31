package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.MailFilter
import org.jsoup.Jsoup
import java.net.URLEncoder

class ClickFilter(private val clickUrl: String) : MailFilter {
    override fun filter(html: String, context: MailContext): String {
        val doc = Jsoup.parse(html)
        doc.select("a").forEach { link ->
            val href = link.attr("href").trim().lowercase()
            if (href.startsWith("http://") || href.startsWith("https://")) {
                link.attr("href", decorate(href, context))
            }
        }
        return doc.html()
    }

    private fun decorate(href: String, context: MailContext): String =
        "$clickUrl?" +
            (context.storyId?.let { "story-id=$it&" } ?: "") +
            ("url=" + URLEncoder.encode(href, "utf-8"))
}
