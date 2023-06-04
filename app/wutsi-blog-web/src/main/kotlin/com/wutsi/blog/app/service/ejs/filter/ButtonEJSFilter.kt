package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.app.service.ejs.EJSFilter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ButtonEJSFilter : EJSFilter {
    override fun filter(html: Document) {
        html.select("div.button").forEach { filter(it) }
    }

    private fun filter(div: Element) {
        val img = div.selectFirst("a") ?: return

        if (div.hasClass("large")) {
            div.addClass("text-center")
        }

        img.addClass("btn")
        img.addClass("btn-primary")
        if (div.hasClass("large")) {
            img.addClass("btn-lg")
        }
        if (div.hasClass("stretched")) {
            img.addClass("btn-block")
        }
    }
}
