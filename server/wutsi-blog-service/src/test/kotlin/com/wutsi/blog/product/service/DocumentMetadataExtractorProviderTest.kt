package com.wutsi.blog.product.service

import com.nhaarman.mockitokotlin2.mock
import com.wutsi.blog.product.service.metadata.CBZMetadataExtractor
import com.wutsi.blog.product.service.metadata.EPUBMetadataExtractor
import com.wutsi.blog.product.service.metadata.PDFMetadataExtractor
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DocumentMetadataExtractorProviderTest {
    private val pdf = mock<PDFMetadataExtractor>()
    private val epub = mock<EPUBMetadataExtractor>()
    private val cbz = mock<CBZMetadataExtractor>()
    private val provider = DocumentMetadataExtractorProvider(pdf, epub, cbz)

    @Test
    fun `content-type - pdf`() {
        val extractor = provider.get("application/pdf", "")
        assertEquals(pdf, extractor)
    }

    @Test
    fun `content-type -  epub`() {
        val extractor = provider.get("application/epub+zip", "")
        assertEquals(epub, extractor)
    }

    @Test
    fun `content-type -  cbz`() {
        val extractor = provider.get("application/x-cdisplay", "")
        assertEquals(cbz, extractor)
    }

    @Test
    fun `content-type -  octet-stream - cbz`() {
        val extractor = provider.get("application/octet-stream", "file.cbz")
        assertEquals(cbz, extractor)
    }

    @Test
    fun `content-type -  txt`() {
        val extractor = provider.get("text/plain", "")
        assertNull(extractor)
    }
}
