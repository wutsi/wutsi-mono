package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.MailFilter
import com.wutsi.platform.core.image.ImageService
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class ImageFilter(private val imageService: ImageService) : MailFilter {
    override fun filter(html: String, context: MailContext): String {
        val doc = Jsoup.parse(html)
        doc.select(".story-content figure").forEach { figure ->
            applyStyle(figure, "margin: 0; text-align: center")
        }
        doc.select(".story-content figure img").forEach { img ->
            applyStyle(img, "max-width: 100%; margin: 0 auto;")
            img.removeAttr("width")
            img.removeAttr("height")
            img.attr("src", source(img))
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

    private fun source(elt: Element): String {
        val src = elt.attr("src")
        return imageService.transform(src)
    }
}
