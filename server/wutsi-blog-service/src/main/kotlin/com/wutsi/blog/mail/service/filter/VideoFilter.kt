package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.MailFilter
import org.jsoup.Jsoup

class VideoFilter(private val assetUrl: String) : MailFilter {
    override fun filter(html: String, context: MailContext): String {
        val doc = Jsoup.parse(html)
        doc.select(".play-icon").forEach {
            it.html(
                "<img width=\"32\" src=\"$assetUrl/assets/wutsi/img/mail/play-video.png\" style=\"vertical-align: middle\"/>",
            )
        }
        return doc.html()
    }
}
