package com.wutsi.blog.client.extractor

import com.wutsi.editorjs.dom.EJSDocument

data class ExtractWebPageResponse(
    val page: WebPageDto = WebPageDto(),
    val editorjs: EJSDocument = EJSDocument(),
)
