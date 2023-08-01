package com.wutsi.blog.app.service.ejs

import com.wutsi.blog.app.model.StoryModel
import org.jsoup.nodes.Document

class EJSFilterSet(private val filters: List<EJSFilter>) : EJSFilter {
    override fun filter(story: StoryModel, html: Document) {
        filters.forEach { it.filter(story, html) }
    }
}
