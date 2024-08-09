package com.wutsi.blog.app.page.admin.store

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.admin.DraftControllerTest
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.Category
import com.wutsi.blog.product.dto.DeleteProductCommand
import com.wutsi.blog.product.dto.GetProductResponse
import com.wutsi.blog.product.dto.Product
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import com.wutsi.blog.product.dto.UpdateProductAttributeCommand
import com.wutsi.platform.core.storage.MimeTypes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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
        fileContentType = MimeTypes.EPUB,
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

        setupLoggedInUser(DraftControllerTest.BLOG_ID, blog = true, storeId = STORE_ID)
        doReturn(GetProductResponse(product)).whenever(productBackend).get(any())
    }

    @Test
    fun product() {
        doReturn(GetProductResponse(product.copy(fileContentType = "text/plain"))).whenever(productBackend).get(any())

        navigate(url("/me/store/products/${product.id}"))
        assertCurrentPageIs(PageName.STORE_PRODUCT)

        assertElementAttributeEndsWith("#product-link", "href", product.slug)
        assertElementAttribute("#product-link-copy", "href", "javascript: copy_link('product-link')")
        testUpdate(product.id, "title", product.title, "Les nuits chaudes")
        scrollToBottom()
        testUpdate(product.id, "description", product.description, "Hot and Steamy!")
        testUpdate(product.id, "liretama_url", product.liretamaUrl, "https://www.liretama.com/livres/les-nuits-chaudes")

        assertElementNotPresent("#btn-preview")
        assertElementNotPresent("#btn-delete")
    }

    @Test
    fun delete() {
        doReturn(GetProductResponse(product.copy(status = ProductStatus.DRAFT))).whenever(productBackend).get(any())

        navigate(url("/me/store/products/${product.id}"))

        click("#btn-delete")
        driver.switchTo().alert().accept()

        Thread.sleep(1000)
        val cmd = argumentCaptor<DeleteProductCommand>()
        verify(productBackend).delete(cmd.capture())
        assertEquals(product.id, cmd.firstValue.productId)

        assertCurrentPageIs(PageName.STORE_PRODUCTS)
    }

    @Test
    fun epub() {
        navigate(url("/me/store/products/${product.id}"))

        assertCurrentPageIs(PageName.STORE_PRODUCT)

        click("#btn-preview")
        assertCurrentPageIs(PageName.STORE_PRODUCT_PREVIEW)
    }

    @Test
    fun cbz() {
        doReturn(
            GetProductResponse(product.copy(fileContentType = MimeTypes.CBZ, status = ProductStatus.DRAFT))
        ).whenever(productBackend).get(any())

        navigate(url("/me/store/products/${product.id}"))

        assertCurrentPageIs(PageName.STORE_PRODUCT)

        assertElementNotPresent("#product-link")
        assertElementNotPresent("#product-link-copy")

        click("#btn-preview")
        assertCurrentPageIs(PageName.STORE_PRODUCT_PREVIEW)
    }

    private fun testUpdate(
        productId: Long,
        name: String,
        originalValue: String?,
        newValue: String,
        error: String? = null,
    ) {
        val selector = "#$name-form"

        // Test current value
        assertElementAttribute("$selector .form-control", "value", originalValue ?: "")

        // Change
        click("$selector .btn-edit")
        input("$selector .form-control", newValue)
        click("$selector .btn-save", 1000)

        // Verify changes
        assertElementAttribute("$selector .form-control", "value", newValue)
        if (error == null) {
            assertElementHasClass("$selector .alert-danger", "hidden")
        } else {
            assertElementHasNotClass("$selector .alert-danger", "hidden")
            assertElementPresent("$selector .alert-danger")
        }

        // Verify backend call
        val cmd = argumentCaptor<UpdateProductAttributeCommand>()
        verify(productBackend).updateAttribute(cmd.capture())
        assertEquals(name, cmd.firstValue.name)
        assertEquals(newValue, cmd.firstValue.value)
        assertEquals(productId, cmd.firstValue.productId)
        reset(productBackend)
    }
}
