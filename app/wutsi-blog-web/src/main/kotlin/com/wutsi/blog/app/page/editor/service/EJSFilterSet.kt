package com.wutsi.blog.app.page.editor.service

import org.jsoup.nodes.Document

class EJSFilterSet(private val filters: List<Filter>) : Filter {

    override fun filter(html: Document) {
        filters.forEach { it.filter(html) }
    }
}
