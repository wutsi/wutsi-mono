package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.HtmlImageModel
import com.wutsi.blog.app.service.HtmlImageService
import org.springframework.stereotype.Service

@Service
class HtmlImageModelMapper(private val service: HtmlImageService) {
    fun toHtmlImageMapper(src: String?) = if (src == null || src.isEmpty()) {
        null
    } else {
        HtmlImageModel(
            src = src,
            srcset = service.srcset(src),
            sizes = service.sizes(),
        )
    }
}
