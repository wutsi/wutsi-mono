package com.wutsi.blog.app.service.ejs

import org.jsoup.nodes.Document

class EJSEJSFilterSet(private val filters: List<EJSFilter>) : EJSFilter {

    override fun filter(html: Document) {
        filters.forEach { it.filter(html) }
    }
}
