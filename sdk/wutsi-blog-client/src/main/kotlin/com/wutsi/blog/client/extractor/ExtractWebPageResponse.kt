package com.wutsi.blog.client.extractor

import com.wutsi.blog.story.dto.WebPage
import com.wutsi.editorjs.dom.EJSDocument

data class ExtractWebPageResponse(
    val page: WebPage = WebPage(),
    val editorjs: EJSDocument = EJSDocument(),
)
