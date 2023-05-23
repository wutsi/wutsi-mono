package com.wutsi.blog.app.page.editor.service

import com.wutsi.blog.app.page.editor.model.EJSLinkMeta

interface LinkExtractor {
    fun accept(url: String): Boolean

    fun extract(url: String): EJSLinkMeta
}
