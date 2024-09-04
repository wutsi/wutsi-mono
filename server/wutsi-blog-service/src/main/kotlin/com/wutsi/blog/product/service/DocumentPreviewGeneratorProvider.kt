package com.wutsi.blog.product.service

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.service.metadata.EPUBPreviewGenerator
import com.wutsi.platform.core.storage.MimeTypes
import org.springframework.stereotype.Service

@Service
class DocumentPreviewGeneratorProvider(
    private val epub: EPUBPreviewGenerator,
) {
    fun get(product: ProductEntity): DocumentPreviewGenerator? =
        when (product.fileContentType) {
            MimeTypes.EPUB -> epub
            else -> null
        }
}
