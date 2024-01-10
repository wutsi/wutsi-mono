package com.wutsi.blog.product.service

import com.nhaarman.mockitokotlin2.mock
import com.wutsi.blog.product.service.metadata.EPUBMetadataExtractor
import com.wutsi.blog.product.service.metadata.PDFMetadataExtractor
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DocumentMetadataExtractorProviderTest {
    private val pdf = mock<PDFMetadataExtractor>()
    private val epub = mock<EPUBMetadataExtractor>()
    private val provider = DocumentMetadataExtractorProvider(pdf, epub)

    @Test
    fun `content-type - pdf`() {
        val extractor = provider.get("application/pdf")
        assertEquals(pdf, extractor)
    }

    @Test
    fun `content-type -  epub`() {
        val extractor = provider.get("application/epub+zip")
        assertEquals(epub, extractor)
    }
    
    @Test
    fun `content-type -  txt`() {
        val extractor = provider.get("text/plain")
        assertNull(extractor)
    }
}
