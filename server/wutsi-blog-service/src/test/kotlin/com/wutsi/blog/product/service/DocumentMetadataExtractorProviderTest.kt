package com.wutsi.blog.product.service

import com.nhaarman.mockitokotlin2.mock
import com.wutsi.blog.product.service.metadata.PDFMetadataExtractor
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DocumentMetadataExtractorProviderTest {
    private val pdf = mock<PDFMetadataExtractor>()
    private val provider = DocumentMetadataExtractorProvider(pdf)

    @Test
    fun `content-type - pdf`() {
        val extractor = provider.get("application/pdf")
        assertEquals(pdf, extractor)
    }

    @Test
    fun `content-type -  txt`() {
        val extractor = provider.get("application/pdf")
        assertNotNull(extractor)
    }
}
