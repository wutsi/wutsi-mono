package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.app.service.RequestContext
import org.jsoup.nodes.Element

class SubscribeBannerEJSFilter(private val requestContext: RequestContext) : AbstractButtonBannerEJSFilter() {
    override fun shouldDecorate(link: Element): Boolean {
        val href = link.attr("href")
        return href.contains("/subscribe?return-url=")
    }

    override fun decorate(div: Element) {
        div.addClass("padding")
        div.addClass("border")
        div.addClass("subscription-container")

        val txt = div.ownerDocument()!!.createElement("div")
        txt.addClass("margin-bottom")
        txt.text(requestContext.getMessage("label.please_subscribe"))
        div.prependChild(txt)

        div.selectFirst("a")?.attr("rel", "nofollow")
        div.selectFirst("a")?.addClass("btn-follow")
    }
}
