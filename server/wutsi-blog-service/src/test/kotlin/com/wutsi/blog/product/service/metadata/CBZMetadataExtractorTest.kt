package com.wutsi.blog.product.service.metadata

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.product.dao.PageRepository
import com.wutsi.blog.product.domain.PageEntity
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URL
import kotlin.test.assertEquals

class CBZMetadataExtractorTest {
    private val dao = mock<PageRepository> {}
    private val storage = mock<StorageService> { }
    private val extractor = CBZMetadataExtractor(dao, storage)

    @Test
    fun extract() {
        // Given
        val uri = EPUBMetadataExtractorTest::class.java.getResource("/document.cbz").toURI()
        val product = ProductEntity()

        doReturn(URL("https://g.com/11.jpg"))
            .doReturn(URL("https://g.com/12.png"))
            .doReturn(URL("https://g.com/13.png"))
            .doReturn(URL("https://g.com/14.png"))
            .doReturn(URL("https://g.com/00.png"))
            .whenever(storage)
            .store(any(), any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(emptyList<PageEntity>()).whenever(dao).findByProduct(any())
        doReturn(null).whenever(dao).findByProductAndNumber(any(), any())
        doReturn(PageEntity()).whenever(dao).save(any())

        // When
        extractor.extract(File(uri), product)

        // Then
        assertEquals(5, product.numberOfPages)

        val args = argumentCaptor<Iterable<PageEntity>>()
        verify(dao).saveAll(args.capture())

        val pages = args.firstValue.toList()
        assertEquals(5, pages.size)

        assertEquals(1, pages[0].number)
        assertEquals("https://g.com/00.png", pages[0].contentUrl)
        assertEquals("image/png", pages[0].contentType)

        assertEquals(2, pages[1].number)
        assertEquals("https://g.com/11.jpg", pages[1].contentUrl)
        assertEquals("image/jpeg", pages[1].contentType)

        assertEquals(3, pages[2].number)
        assertEquals("https://g.com/12.png", pages[2].contentUrl)
        assertEquals("image/png", pages[2].contentType)

        assertEquals(4, pages[3].number)
        assertEquals("https://g.com/13.png", pages[3].contentUrl)
        assertEquals("image/png", pages[3].contentType)

        assertEquals(5, pages[4].number)
        assertEquals("https://g.com/14.png", pages[4].contentUrl)
        assertEquals("image/png", pages[4].contentType)
    }
}