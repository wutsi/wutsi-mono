package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.ejs.EJSFilter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class AdsEJSFilter : EJSFilter {
    override fun filter(story: StoryModel, html: Document) {
        html.select("div.ad")
            .forEach {
                filter(story, it)
            }
    }

    private fun filter(story: StoryModel, a: Element) {
        a.addClass("ads-banner-container")
        a.attr("wutsi-ads-blog-id", story.user.id.toString())
        a.attr("wutsi-ads-type",
            listOf(AdsType.BOX, AdsType.BOX_2X)
                .map { it.name }
                .joinToString(",")
        )
    }
}
