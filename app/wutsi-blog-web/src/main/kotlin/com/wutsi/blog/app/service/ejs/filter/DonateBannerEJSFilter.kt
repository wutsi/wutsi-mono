package com.wutsi.blog.app.service.ejs.filter

import com.wutsi.blog.app.service.RequestContext
import org.jsoup.nodes.Element

class DonateBannerEJSFilter(private val requestContext: RequestContext) : AbstractButtonBannerEJSFilter() {
    override fun decorate(div: Element) {
        div.addClass("padding")
        div.addClass("donation-container")

        val txt = div.ownerDocument()!!.createElement("div")
        txt.addClass("margin-bottom")
        txt.text(requestContext.getMessage("label.please_support_us"))
        div.prependChild(txt)

        div.selectFirst("a")?.attr("rel", "nofollow")
    }

    override fun shouldDecorate(link: Element): Boolean {
        val href = link.attr("href")
        return href.contains("/donate")
    }
}
