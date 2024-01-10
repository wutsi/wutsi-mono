package com.wutsi.blog.product.service.metadata

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.service.DocumentMetadataExtractor
import org.apache.tika.language.detect.LanguageDetector
import org.springframework.stereotype.Service
import java.io.File

@Service
class EPUBMetadataExtractor(
    private val languageDetector: LanguageDetector
) : DocumentMetadataExtractor {
    override fun extract(file: File, product: ProductEntity) {
        product.numberOfPages = null

        val txt = "${product.title}.${product.description}"
        product.language = languageDetector.detect(txt).language
    }
}
