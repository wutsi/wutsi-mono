package com.wutsi.blog.product.service

import com.wutsi.blog.product.service.metadata.CBZMetadataExtractor
import com.wutsi.blog.product.service.metadata.EPUBMetadataExtractor
import com.wutsi.blog.product.service.metadata.PDFMetadataExtractor
import com.wutsi.platform.core.storage.MimeTypes
import org.springframework.stereotype.Service

@Service
class DocumentMetadataExtractorProvider(
    private val pdf: PDFMetadataExtractor,
    private val epub: EPUBMetadataExtractor,
    private val cbz: CBZMetadataExtractor,
) {
    fun get(contentType: String, fileName: String): DocumentMetadataExtractor? =
        when (contentType) {
            MimeTypes.PDF -> pdf
            MimeTypes.EPUB -> epub
            MimeTypes.CBZ -> cbz
            "", MimeTypes.OCTET_STREAM -> if (fileName.lowercase().endsWith(".cbz")) {
                cbz
            } else {
                null
            }

            else -> null
        }
}
