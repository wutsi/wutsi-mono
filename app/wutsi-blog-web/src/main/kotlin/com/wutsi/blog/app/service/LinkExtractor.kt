package com.wutsi.blog.app.service

import com.wutsi.blog.app.page.admin.model.EJSLinkMeta

interface LinkExtractor {
    fun accept(url: String): Boolean

    fun extract(url: String): EJSLinkMeta
}
