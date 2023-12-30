package com.wutsi.blog.product.service.metadata

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.service.DocumentMetadataExtractor
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.tika.language.detect.LanguageDetector
import org.springframework.stereotype.Service
import java.io.File
import kotlin.math.min

@Service
class PDFMetadataExtractor(
    private val languageDetector: LanguageDetector
) : DocumentMetadataExtractor {
    override fun extract(file: File, product: ProductEntity) {
        val doc = Loader.loadPDF(file)
        val stripper = PDFTextStripper()
        stripper.startPage = 1
        stripper.endPage = min(5, doc.numberOfPages)
        val txt = stripper.getText(doc)

        product.numberOfPages = doc.numberOfPages
        product.language = languageDetector.detect(txt).language
    }
}
