package com.wutsi.blog.product.service

import com.wutsi.blog.product.service.metadata.EPUBMetadataExtractor
import com.wutsi.blog.product.service.metadata.PDFMetadataExtractor
import org.springframework.stereotype.Service

@Service
class DocumentMetadataExtractorProvider(
    private val pdf: PDFMetadataExtractor,
    private val epub: EPUBMetadataExtractor
) {
    fun get(contentType: String): DocumentMetadataExtractor? =
        when (contentType) {
            "application/pdf" -> pdf
            "application/epub+zip" -> epub
            else -> null
        }
}
