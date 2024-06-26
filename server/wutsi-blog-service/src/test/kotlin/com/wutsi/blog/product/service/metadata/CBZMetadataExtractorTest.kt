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

        doReturn(
            listOf(
                PageEntity(id = 1, number = 1),
                PageEntity(id = 2, number = 2),
                PageEntity(id = 3, number = 3),
                PageEntity(id = 10, number = 10),
                PageEntity(id = 11, number = 11),
            )
        ).whenever(dao).findByProduct(any())

        doReturn(PageEntity()).whenever(dao).save(any())

        // When
        extractor.extract(File(uri), product)

        // Then
        assertEquals(5, product.numberOfPages)

        val save = argumentCaptor<Iterable<PageEntity>>()
        verify(dao).saveAll(save.capture())

        val pages = save.firstValue.toList()
        assertEquals(5, pages.size)
        assertEquals(1, pages[0].number)
        assertEquals(null, pages[0].id)
        assertEquals("https://g.com/00.png", pages[0].contentUrl)
        assertEquals("image/png", pages[0].contentType)
        assertEquals(1L, pages[1].id)
        assertEquals(2, pages[1].number)
        assertEquals("https://g.com/11.jpg", pages[1].contentUrl)
        assertEquals("image/jpeg", pages[1].contentType)
        assertEquals(2L, pages[2].id)
        assertEquals(3, pages[2].number)
        assertEquals("https://g.com/12.png", pages[2].contentUrl)
        assertEquals("image/png", pages[2].contentType)
        assertEquals(3L, pages[3].id)
        assertEquals(4, pages[3].number)
        assertEquals("https://g.com/13.png", pages[3].contentUrl)
        assertEquals("image/png", pages[3].contentType)
        assertEquals(null, pages[4].id)
        assertEquals(5, pages[4].number)
        assertEquals("https://g.com/14.png", pages[4].contentUrl)
        assertEquals("image/png", pages[4].contentType)

        val del = argumentCaptor<Iterable<PageEntity>>()
        verify(dao).deleteAll(del.capture())
        val xpages = del.firstValue.toList()
        assertEquals(2, xpages.size)
        assertEquals(10, xpages[0].number)
        assertEquals(11, xpages[1].number)
    }
}