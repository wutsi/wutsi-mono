package com.wutsi.blog.product.service

import com.wutsi.blog.product.domain.ProductEntity
import java.io.File

interface DocumentMetadataExtractor {
    fun extract(file: File, product: ProductEntity)
}
