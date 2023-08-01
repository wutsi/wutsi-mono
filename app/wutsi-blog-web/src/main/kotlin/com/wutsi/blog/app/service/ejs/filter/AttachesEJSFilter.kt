package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.ejs.EJSFilter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URLEncoder
import java.util.Base64

class AttachesEJSFilter : EJSFilter {
    override fun filter(story: StoryModel, html: Document) {
        html.select("a.attaches")
            .forEach {
                filter(story, it)
            }
    }

    private fun filter(story: StoryModel, a: Element) {
        val href = a.attr("href")
        val filename = a.attr("title")
        val xhref = "/attachment/download?f=" + URLEncoder.encode(filename, "utf-8") +
            "&l=" + URLEncoder.encode(Base64.getEncoder().encodeToString(href.toByteArray())) +
            "&s=${story.id}"

        a.attr("href", xhref)
    }
}
