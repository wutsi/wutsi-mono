package com.wutsi.blog.product.service

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.ProductType
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
    fun get(product: ProductEntity): DocumentMetadataExtractor? =
        when (product.fileContentType) {
            MimeTypes.PDF -> pdf
            MimeTypes.EPUB -> epub
            MimeTypes.CBZ -> cbz
            "", MimeTypes.OCTET_STREAM -> if (product.fileUrl?.lowercase()?.endsWith(".cbz") == true) {
                cbz
            } else {
                null
            }

            MimeTypes.ZIP -> if (product.type == ProductType.COMICS) {
                cbz
            } else {
                null
            }

            else -> null
        }
}
