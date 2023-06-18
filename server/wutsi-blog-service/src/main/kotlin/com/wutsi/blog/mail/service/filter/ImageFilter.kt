package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.MailFilter
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class ImageFilter : MailFilter {
    override fun filter(html: String, context: MailContext): String {
        val doc = Jsoup.parse(html)
        doc.select(".story-content figure").forEach { applyStyle(it, "margin: 0; text-align: center") }
        doc.select(".story-content img").forEach {
            applyStyle(it, "max-width: 100%; margin: 0 auto;")
            it.removeAttr("width")
            it.removeAttr("height")
        }
        doc.select(".story-content figcaption").forEach {
            applyStyle(it, "text-decoration: underline; font-size: 0.8em;")
        }
        return doc.html()
    }

    private fun applyStyle(elt: Element, style: String) {
        if (elt.hasAttr("style")) {
            elt.attr("style", elt.attr("style") + ";$style")
        } else {
            elt.attr("style", style)
        }
    }
}
