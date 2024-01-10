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

class EPUBMetadataExtractorTest {
    private val languageDetector = mock<LanguageDetector>()
    private val extractor = EPUBMetadataExtractor(languageDetector)

    @Test
    fun extract() {
        // GIVEN
        val uri = EPUBMetadataExtractorTest::class.java.getResource("/document.epub").toURI()
        val file = File(uri)
        val product = ProductEntity()
        val result = LanguageResult("fr", LanguageConfidence.HIGH, 0.5f)
        doReturn(result).whenever(languageDetector).detect(any())

        // WHEN
        extractor.extract(file, product)

        // THEN
        assertEquals(null, product.numberOfPages)
        assertEquals("fr", product.language)
    }
}
