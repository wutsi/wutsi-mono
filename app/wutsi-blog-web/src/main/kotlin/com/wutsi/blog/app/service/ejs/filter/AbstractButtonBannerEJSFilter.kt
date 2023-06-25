package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.app.service.ejs.EJSFilter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

abstract class AbstractButtonBannerEJSFilter : EJSFilter {
    protected abstract fun shouldDecorate(link: Element): Boolean
    protected abstract fun decorate(div: Element)

    override fun filter(html: Document) {
        html.select("div.button").forEach { filter(it) }
    }

    private fun filter(div: Element) {
        val link = div.selectFirst("a") ?: return

        if (shouldDecorate(link)) {
            decorate(div)
        }
    }
}
