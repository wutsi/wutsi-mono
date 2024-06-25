package com.wutsi.blog.product.service.metadata

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
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

        doReturn(URL("https://g.com/1.png"))
            .doReturn(URL("https://g.com/2.png"))
            .doReturn(URL("https://g.com/3.png"))
            .doReturn(URL("https://g.com/4.png"))
            .doReturn(URL("https://g.com/5.png"))
            .whenever(storage)
            .store(any(), any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(null).whenever(dao).findByProductAAndNumber(any(), any())
        doReturn(PageEntity()).whenever(dao).save(any())

        // When
        extractor.extract(File(uri), product)

        // Then
        assertEquals(5, product.numberOfPages)

        verify(dao, times(5)).save(any())
    }
}