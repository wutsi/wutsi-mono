package com.wutsi.blog.app.page.admin.store

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.admin.DraftControllerTest
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.Category
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.blog.product.dto.SearchCategoryResponse
import com.wutsi.blog.product.dto.SearchProductResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StoreProductsControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 1L
        const val STORE_ID = "100"
    }

    private val products = listOf(
        ProductSummary(
            id = 100,
            title = "Product 100",
            imageUrl = "https://picsum.photos/1200/600",
            fileUrl = "https://www.google.ca/123.pdf",
            storeId = STORE_ID,
            price = 1000,
            currency = "XAF",
            status = ProductStatus.PUBLISHED,
            available = true,
            slug = "/product/100/product-100",
            categoryId = 110,
        ),
        ProductSummary(
            id = 200,
            title = "Product 200",
            imageUrl = "https://picsum.photos/1200/600",
            fileUrl = "https://www.google.ca/123.pdf",
            storeId = STORE_ID,
            price = 1000,
            currency = "XAF",
            status = ProductStatus.PUBLISHED,
            available = true,
            slug = "/product/200/product-200",
            categoryId = 120,
        )
    )

    private val categories = listOf(
        Category(
            id = 100,
            title = "Art",
            longTitle = "Art"
        ),
        Category(
            id = 110,
            title = "Art",
            longTitle = "Art > Painting"
        ),
        Category(
            id = 120,
            title = "Art",
            longTitle = "Art > Drawaing"
        )
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchProductResponse(products)).whenever(productBackend).search(any())
        doReturn(SearchCategoryResponse(categories)).whenever(categoryBackend).search(any())
    }

    @Test
    fun products() {
        setupLoggedInUser(DraftControllerTest.BLOG_ID, blog = true, storeId = STORE_ID)

        navigate(url("/me/store/products"))

        assertCurrentPageIs(PageName.STORE_PRODUCTS)
    }

    @Test
    fun notABlog() {
        setupLoggedInUser(DraftControllerTest.BLOG_ID, blog = false, storeId = STORE_ID)

        navigate(url("/me/store/products"))

        assertCurrentPageIs(PageName.ERROR)
    }

    @Test
    fun hasNoStore() {
        setupLoggedInUser(DraftControllerTest.BLOG_ID, blog = true, storeId = null)

        navigate(url("/me/store/products"))

        assertCurrentPageIs(PageName.ERROR)
    }
}
