package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.app.service.ejs.EJSFilter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

abstract class AbstractButtonBannerEJSFilter : EJSFilter {
    protected abstract fun shouldDecorate(link: Element): Boolean

    override fun filter(html: Document) {
        html.select("div.button").forEach { filter(it) }
    }

    protected open fun decorate(div: Element) {
        div.addClass("padding")
        div.addClass("box-filled-highlight-light")
    }

    private fun filter(div: Element) {
        val link = div.selectFirst("a") ?: return

        if (shouldDecorate(link)) {
            decorate(div)
        }
    }
}
