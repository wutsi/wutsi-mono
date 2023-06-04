package com.wutsi.blog.app.service.ejs

import org.jsoup.nodes.Document

interface EJSFilter {
    fun filter(html: Document)
}
