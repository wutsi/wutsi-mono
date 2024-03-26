package com.wutsi.blog.app.page.admin.store

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.admin.DraftControllerTest
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.Category
import com.wutsi.blog.product.dto.GetProductResponse
import com.wutsi.blog.product.dto.Product
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StoreProductControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 1L
        const val STORE_ID = "100"
    }

    private val product = Product(
        id = 100,
        title = "Product 100",
        imageUrl = "https://picsum.photos/1200/600",
        fileUrl = "https://www.google.ca/123.epub",
        storeId = STORE_ID,
        price = 1000,
        currency = "XAF",
        status = ProductStatus.PUBLISHED,
        available = true,
        slug = "/product/100/product-100",
        description = "This is the description of the product",
        language = "en",
        fileContentType = "application/epub+zip",
        type = ProductType.EBOOK,
        category = Category(
            id = 100,
            parentId = null,
            title = "Art",
            longTitle = "Art",
            titleFrench = "Arg",
            longTitleFrench = "Art",
            titleFrenchAscii = "Arg"
        ),
        fileContentLength = 12 * 1024
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetProductResponse(product)).whenever(productBackend).get(any())
    }

    @Test
    fun product() {
        setupLoggedInUser(DraftControllerTest.BLOG_ID, blog = true, storeId = STORE_ID)
        doReturn(GetProductResponse(product.copy(fileContentType = "text/plain"))).whenever(productBackend).get(any())

        navigate(url("/me/store/products/${product.id}"))

        assertCurrentPageIs(PageName.STORE_PRODUCT)
        assertElementNotPresent("#btn-preview")
    }

    @Test
    fun `cant preview non epub`() {
        setupLoggedInUser(DraftControllerTest.BLOG_ID, blog = true, storeId = STORE_ID)

        navigate(url("/me/store/products/${product.id}"))

        assertCurrentPageIs(PageName.STORE_PRODUCT)

        click("#btn-preview")
        assertCurrentPageIs(PageName.STORE_PRODUCT_PREVIEW)
    }
}
