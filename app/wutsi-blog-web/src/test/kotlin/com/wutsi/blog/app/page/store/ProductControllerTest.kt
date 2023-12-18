package com.wutsi.blog.app.page.store

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.GetProductResponse
import com.wutsi.blog.product.dto.GetStoreResponse
import com.wutsi.blog.product.dto.Product
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.Store
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.User
import com.wutsi.tracking.manager.dto.PushTrackRequest
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProductControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 1L
        const val STORE_ID = "100"
    }

    private val product = Product(
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
        orderCount = 111L,
        totalSales = 15000L,
        fileContentType = "application/pdf",
        fileContentLength = 220034L,
        description = "This is the description of the product",
        externalId = "100",
    )

    private val blog = User(
        id = BLOG_ID,
        storeId = STORE_ID,
        walletId = "123",
        name = "pragmaticdev",
        fullName = "Pragmatic Dev",
        email = "pragmaticdev@gmail.com",
        pictureUrl = "https://picsum.photos/200/200",
        blog = true,
        biography = "This is an example of bio",
        websiteUrl = "https://www.google.ca",
        language = "en",
        facebookId = "pragmaticdev",
        twitterId = "pragmaticdev",
        publishStoryCount = 10,
    )

    private val store = Store(
        id = STORE_ID,
        currency = "XAF",
        userId = BLOG_ID,
    )

    override fun setUp() {
        super.setUp()

        doReturn(GetProductResponse(product)).whenever(productBackend).get(any())

        doReturn(GetStoreResponse(store)).whenever(storeBackend).get(any())

        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.id)
        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.name)
    }

    @Test
    fun anonymous() {
        navigate(url("/product/${product.id}"))

        assertCurrentPageIs(PageName.SHOP_PRODUCT)

        // Tracking
        val track = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(track.capture())
        assertEquals(product.id.toString(), track.firstValue.productId.toString())
        assertEquals(PageName.SHOP_PRODUCT, track.firstValue.page)
        assertEquals("productview", track.firstValue.event)
        assertEquals(
            driver.findElement(By.cssSelector("head meta[name='wutsi:hit_id")).getAttribute("content"),
            track.firstValue.correlationId,
        )
        assertNull(track.firstValue.accountId)
    }

    @Test
    fun loggedIn() {
        val user = setupLoggedInUser(userId = 333L)

        navigate(url(product.slug))

        assertCurrentPageIs(PageName.SHOP_PRODUCT)

        // Tracking
        val track = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(track.capture())
        assertEquals(product.id.toString(), track.firstValue.productId.toString())
        assertEquals(PageName.SHOP_PRODUCT, track.firstValue.page)
        assertEquals("productview", track.firstValue.event)
        assertEquals(
            driver.findElement(By.cssSelector("head meta[name='wutsi:hit_id")).getAttribute("content"),
            track.firstValue.correlationId,
        )
        assertEquals(user.id.toString(), track.firstValue.accountId)
    }

    @Test
    fun shareToFacebook() {
        // THEN
        navigate(url(product.slug))
        click("#btn-share")

        // THEN
        assertElementVisible("#share-modal")
        Thread.sleep(1000)
        click("#share-modal a[data-target=facebook]")
        Thread.sleep(1000)
    }
}
