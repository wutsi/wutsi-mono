package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.ejs.EJSFilter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URLEncoder

class LinkEJSFilter(
    private val websiteUrl: String,
) : EJSFilter {
    override fun filter(story: StoryModel, html: Document) {
        html.select("a").forEach { filter(story, it) }
    }

    private fun filter(story: StoryModel, img: Element) {
        val href = img.attr("href")
        if (href.startsWith("http://") || href.startsWith("https://")) {
            val url = "$websiteUrl/wclick?story-id=${story.id}&url=" + URLEncoder.encode(href, "utf-8")
            img.attr("href", url)
            img.attr("rel", "nofollow")
        }
    }
}
