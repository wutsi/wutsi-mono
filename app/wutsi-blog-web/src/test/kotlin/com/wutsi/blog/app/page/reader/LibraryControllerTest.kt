package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.store.ShopControllerTest
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.BookSummary
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.blog.product.dto.SearchBookResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LibraryControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 1L
        const val USER_ID = 555L
    }

    private val books = listOf(
        BookSummary(
            id = 100,
            userId = USER_ID,
            product = ProductSummary(
                id = 100,
                title = "Product 100",
                imageUrl = "https://picsum.photos/1200/600",
                fileUrl = "https://www.google.ca/123.pdf",
                storeId = ShopControllerTest.STORE_ID,
                price = 1000,
                currency = "XAF",
                status = ProductStatus.PUBLISHED,
                available = true,
                slug = "/product/100/product-100",
            )
        ),
        BookSummary(
            id = 200,
            userId = USER_ID,
            product = ProductSummary(
                id = 200,
                title = "Product 200",
                imageUrl = "https://picsum.photos/1200/600",
                fileUrl = "https://www.google.ca/123.pdf",
                storeId = ShopControllerTest.STORE_ID,
                price = 1500,
                currency = "XAF",
                status = ProductStatus.PUBLISHED,
                available = true,
                slug = "/product/200/product-200",
            )
        ),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchBookResponse(books)).whenever(bookBackend).search(any())
    }

    @Test
    fun index() {
        setupLoggedInUser(userId = USER_ID)
        navigate(url("/me/library"))

        assertCurrentPageIs(PageName.LIBRARY)
        assertElementCount(".book-card", 2)
    }

    @Test
    fun requiresLogin() {
        navigate(url("/me/library"))

        assertCurrentPageIs(PageName.LOGIN)
    }
}
