package com.wutsi.blog.product.service.preview

import com.wutsi.blog.product.service.metadata.EPUBPreviewGenerator
import io.documentnode.epub4j.epub.EpubReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EPUBPreviewGeneratorTest {
    @Test
    fun preview() {
        // Given
        val fin = EPUBPreviewGeneratorTest::class.java.getResourceAsStream("/document.epub")
        val file = File.createTempFile("preview", "epub")
        val fout = FileOutputStream(file)
        val done = fout.use {
            EPUBPreviewGenerator().generate(fin, fout)
        }

        // Then
        assertTrue(done)

        val result = FileInputStream(file)
        val book = result.use {
            EpubReader().readEpub(result)
        }
        assertEquals(EPUBPreviewGenerator.MAX_SIZE, book.tableOfContents.size())
    }
}
