package com.wutsi.blog.product.service

import com.wutsi.blog.product.service.metadata.CBZMetadataExtractor
import com.wutsi.blog.product.service.metadata.EPUBMetadataExtractor
import com.wutsi.blog.product.service.metadata.PDFMetadataExtractor
import org.springframework.stereotype.Service

@Service
class DocumentMetadataExtractorProvider(
    private val pdf: PDFMetadataExtractor,
    private val epub: EPUBMetadataExtractor,
    private val cbz: CBZMetadataExtractor,
) {
    companion object {
        const val CONTENT_TYPE_OCTET_STREAM = "application/octet-stream"
    }

    fun get(contentType: String, fileName: String): DocumentMetadataExtractor? =
        when (contentType) {
            PDFMetadataExtractor.CONTENT_TYPE -> pdf
            EPUBMetadataExtractor.CONTENT_TYPE -> epub
            CBZMetadataExtractor.CONTENT_TYPE -> cbz
            CONTENT_TYPE_OCTET_STREAM -> if (fileName.lowercase().endsWith(".cbz")) {
                cbz
            } else {
                null
            }

            else -> null
        }
}
