package com.wutsi.blog.product.service.preview

import com.wutsi.blog.product.service.metadata.EPUBPreviewExtractor
import io.documentnode.epub4j.epub.EpubReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EPUBPreviewExtractorTest {
    @Test
    fun preview() {
        // Given
        val fin = EPUBPreviewExtractorTest::class.java.getResourceAsStream("/document.epub")
        val file = File("preview.epub")
        val fout = FileOutputStream(file)
        val done = fout.use {
            EPUBPreviewExtractor().generate(fin, fout)
        }

        // Then
        assertTrue(done)

        val result = FileInputStream(file)
        val book = result.use {
            EpubReader().readEpub(result)
        }
        assertEquals(EPUBPreviewExtractor.SIZE, book.tableOfContents.size())
    }
}
