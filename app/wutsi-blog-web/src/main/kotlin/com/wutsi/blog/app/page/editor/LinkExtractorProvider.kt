package com.wutsi.blog.app.page.editor.service

import com.wutsi.blog.app.page.editor.service.link.DefaultLinkExtractor
import com.wutsi.blog.app.page.editor.service.link.YouTubeLinkExtractor
import org.springframework.stereotype.Service

@Service
class LinkExtractorProvider(
    private val youtube: YouTubeLinkExtractor,
    private val default: DefaultLinkExtractor,
) {
    private val extractors = arrayListOf<LinkExtractor>(
        youtube,

        default, // Must be the last!!!
    )

    fun get(url: String): LinkExtractor = extractors.find { it.accept(url) }!!
}
