package com.wutsi.blog.app.service.ejs.filter

import org.jsoup.nodes.Element

class SubscribeBannerEJSFilter : AbstractButtonBannerEJSFilter() {
    override fun shouldDecorate(link: Element): Boolean {
        val href = link.attr("href")
        return href.contains("/subscribe?return-url=")
    }

    override fun decorate(div: Element) {
        div.addClass("padding")
        div.addClass("subscription-container")
    }
}
