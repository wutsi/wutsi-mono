package com.wutsi.blog.product.service.metadata

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.product.domain.ProductEntity
import org.apache.tika.language.detect.LanguageConfidence
import org.apache.tika.language.detect.LanguageDetector
import org.apache.tika.language.detect.LanguageResult
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class PDFMetadataExtractorTest {
    private val languageDetector = mock<LanguageDetector>()
    private val extractor = PDFMetadataExtractor(languageDetector)

    @Test
    fun extract() {
        // GIVEN
        val uri = PDFMetadataExtractorTest::class.java.getResource("/document.pdf").toURI()
        val file = File(uri)
        val product = ProductEntity()
        val result = LanguageResult("en", LanguageConfidence.HIGH, 0.5f)
        doReturn(result).whenever(languageDetector).detect(any())

        // WHEN
        extractor.extract(file, product)

        // THEN
        assertEquals(2, product.numberOfPages)
        assertEquals("en", product.language)
    }
}
