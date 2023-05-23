package com.wutsi.blog.app.page.editor.service.filter

import com.wutsi.blog.app.page.editor.service.Filter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class LinkTargetFilter(
    private val websiteUrl: String,
) : Filter {
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
