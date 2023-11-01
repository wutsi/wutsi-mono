package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.MailFilter
import org.jsoup.Jsoup
import java.net.URLEncoder

class LinkFilter(private val clickUrl: String) : MailFilter {
    override fun filter(html: String, context: MailContext): String {
        val doc = Jsoup.parse(html)
        doc.select("a").forEach { link ->
            val href = link.attr("href").trim()
            if (href.startsWith("http://", true) || href.startsWith("https://", true)) {
                link.attr("href", rewrite(href, context))
            }
        }
        return doc.html()
    }

    private fun rewrite(href: String, context: MailContext): String =
        "$clickUrl?utm_medium=email" +
            (context.storyId?.let { "&story-id=$it" } ?: "") +
            ("&url=" + URLEncoder.encode(href, "utf-8"))
}
