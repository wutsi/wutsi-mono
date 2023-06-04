package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.app.service.ejs.EJSFilter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class LinkTargetEJSFilter(
    private val websiteUrl: String,
) : EJSFilter {
    override fun filter(html: Document) {
        html.select("a").forEach { filter(it) }
    }

    private fun filter(img: Element) {
        val href = img.attr("href")
        if (!href.startsWith(websiteUrl)) {
            img.attr("target", "_new")
        }
    }
}
