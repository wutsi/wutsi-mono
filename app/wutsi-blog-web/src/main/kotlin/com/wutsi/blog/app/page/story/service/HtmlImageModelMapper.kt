package com.wutsi.blog.app.page.story.service

import com.wutsi.blog.app.page.story.model.HtmlImageModel
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
