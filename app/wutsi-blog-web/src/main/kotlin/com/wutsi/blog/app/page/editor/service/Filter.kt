package com.wutsi.blog.app.page.editor.service

import org.jsoup.nodes.Document

interface Filter {
    fun filter(html: Document)
}
