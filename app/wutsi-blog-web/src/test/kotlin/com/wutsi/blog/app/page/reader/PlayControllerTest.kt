package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.store.ProductControllerTest
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.Book
import com.wutsi.blog.product.dto.ChangeBookLocationCommand
import com.wutsi.blog.product.dto.GetBookResponse
import com.wutsi.blog.product.dto.Product
import com.wutsi.blog.product.dto.ProductStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class PlayControllerTest : SeleniumTestSupport() {
    companion object {
        const val USER_ID = 555L
        const val USER_ID2 = 111L
    }

    private val book = Book(
        id = 100,
        userId = USER_ID,
        transactionId = "430943049",
        product = Product(
            id = 100,
            title = "Product 100",
            imageUrl = "https://picsum.photos/1200/600",
            fileUrl = "https://github.com/IDPF/epub3-samples/releases/download/20230704/accessible_epub_3.epub",
            storeId = ProductControllerTest.STORE_ID,
            price = 1000,
            currency = "XAF",
            status = ProductStatus.PUBLISHED,
            available = true,
            slug = "/product/100/product-100",
            orderCount = 111L,
            totalSales = 15000L,
            fileContentType = "application/epub+zip",
            fileContentLength = 220034L,
            description = "This is the description of the product",
            externalId = "100",
        )
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetBookResponse(book)).whenever(bookBackend).get(any())
    }

    @Test
    fun play() {
        val xbook = book.copy(product = book.product.copy(fileUrl = "http://localhost:$port/document.epub"))
        doReturn(GetBookResponse(xbook)).whenever(bookBackend).get(any())

        setupLoggedInUser(USER_ID)

        navigate(url("/me/play/${book.id}"))
        Thread.sleep(5000)

        click("#next")
        val request = argumentCaptor<ChangeBookLocationCommand>()
        verify(bookBackend).changeLocation(request.capture())
        assertEquals(book.id, request.firstValue.bookId)
        assertTrue(request.firstValue.location.isNotEmpty())
        assertTrue(request.firstValue.readPercentage > 0)

        click("#btn-back")
        assertCurrentPageIs(PageName.LIBRARY)
    }

    @Test
    fun forbidden() {
        setupLoggedInUser(USER_ID2)

        navigate(url("/me/play/${book.id}"))

        assertCurrentPageIs(PageName.ERROR)
    }

    @Test
    fun notStreamable() {
        val xbook = book.copy(product = book.product.copy(fileContentType = "application/pdf"))
        doReturn(GetBookResponse(xbook)).whenever(bookBackend).get(any())

        navigate(url("/me/play/${book.id}"))

        assertCurrentPageIs(PageName.PLAY)
    }
}
