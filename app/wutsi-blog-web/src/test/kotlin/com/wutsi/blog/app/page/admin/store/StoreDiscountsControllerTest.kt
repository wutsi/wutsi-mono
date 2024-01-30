package com.wutsi.blog.app.page.admin.store

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.admin.DraftControllerTest
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.blog.product.dto.SearchProductResponse
import com.wutsi.blog.product.dto.UpdateStoreDiscountsCommand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StoreDiscountsControllerTest : SeleniumTestSupport() {
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
        )
    )

    override fun setUp() {
        super.setUp()

        doReturn(SearchProductResponse(products)).whenever(productBackend).search(any())
    }

    @Test
    fun discounts() {
        setupLoggedInUser(DraftControllerTest.BLOG_ID, blog = true, storeId = STORE_ID)

        navigate(url("/me/store/products"))

        click("#btn-discounts")
        assertCurrentPageIs(PageName.STORE_DISCOUNTS)

        select("#subscriber-discount", 1)
        select("#first-purchase-discount", 2)
        select("#next-purchase-discount", 3)
        select("#next-purchase-discount-days", 3)
        select("#discount-abandoned-order", 4)
        click("#btn-submit")

        val cmd = argumentCaptor<UpdateStoreDiscountsCommand>()
        verify(storeBackend).updateDiscounts(cmd.capture())
        assertEquals(STORE_ID, cmd.firstValue.storeId)
        assertEquals(5, cmd.firstValue.subscriberDiscount)
        assertEquals(10, cmd.firstValue.firstPurchaseDiscount)
        assertEquals(15, cmd.firstValue.nextPurchaseDiscount)
        assertEquals(28, cmd.firstValue.nextPurchaseDiscountDays)
        assertEquals(20, cmd.firstValue.abandonedOrderDiscount)
        assertEquals(false, cmd.firstValue.enableDonationDiscount)

        assertCurrentPageIs(PageName.STORE_PRODUCTS)
    }
}
