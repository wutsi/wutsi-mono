package com.wutsi.blog.app.page.store

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.Category
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.dto.GetProductResponse
import com.wutsi.blog.product.dto.GetStoreResponse
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.dto.Product
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import com.wutsi.blog.product.dto.SearchOfferResponse
import com.wutsi.blog.product.dto.Store
import com.wutsi.blog.transaction.dto.GetWalletResponse
import com.wutsi.blog.transaction.dto.Wallet
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.User
import com.wutsi.tracking.manager.dto.PushTrackRequest
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProductControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 1L
        const val STORE_ID = "100"
        const val WALLET_ID = "10011"
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
        category = Category(
            id = 120,
            title = "Art",
            longTitle = "Art > Drawing"
        ),
        type = ProductType.EBOOK
    )

    private val offer = Offer(
        productId = product.id,
        price = 800,
        referencePrice = 1000,
        savingAmount = 200,
        savingPercentage = 20,
        discount = Discount(
            type = DiscountType.SUBSCRIBER,
            percentage = 20,
            expiryDate = DateUtils.addDays(Date(), 5)
        )
    )

    private val blog = User(
        id = BLOG_ID,
        storeId = STORE_ID,
        walletId = WALLET_ID,
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
        whatsappId = "23799999999",
        country = "CM",
    )

    private val store = Store(
        id = STORE_ID,
        currency = "XAF",
        userId = BLOG_ID,
    )

    private val wallet = Wallet(
        id = WALLET_ID,
        balance = 1000,
        currency = "XAF",
        userId = BLOG_ID,
        country = "CM",
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetProductResponse(product)).whenever(productBackend).get(any())
        doReturn(SearchOfferResponse(listOf(offer))).whenever(offerBackend).search(any())

        doReturn(GetStoreResponse(store)).whenever(storeBackend).get(STORE_ID)

        doReturn(GetUserResponse(blog)).whenever(userBackend).get(BLOG_ID)
        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.name)

        doReturn(GetWalletResponse(wallet)).whenever(walletBackend).get(WALLET_ID)
    }

    @Test
    fun anonymous() {
        navigate(url("/product/${product.id}"))
        assertCurrentPageIs(PageName.PRODUCT)

        assertElementAttribute("head title", "text", "${product.title} | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", product.description)
        assertElementAttribute("head meta[name='robots']", "content", "index,follow")

        val tags = "Art,Drawing"
        assertElementAttribute("head meta[name='keywords']", "content", tags)

        assertElementAttributeEndsWith("head meta[property='og:url']", "content", product.slug)
        assertElementAttribute("head meta[property='og:image']", "content", product.imageUrl)
        assertElementAttribute("head meta[property='og:site_name']", "content", "Wutsi")
        assertElementAttribute("head meta[property='og:type']", "content", "book")
        assertElementAttribute("head meta[property='book:author']", "content", blog.fullName)
        assertElementCount("head meta[property='book:tag']", tags.split(",").size)

        // Tracking
        val track = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(track.capture())
        assertEquals(product.id.toString(), track.firstValue.productId.toString())
        assertEquals(PageName.PRODUCT, track.firstValue.page)
        assertEquals("productview", track.firstValue.event)
        assertEquals(
            driver.findElement(By.cssSelector("head meta[name='wutsi:hit_id")).getAttribute("content"),
            track.firstValue.correlationId,
        )
        assertNull(track.firstValue.accountId)

        assertElementPresent("#merchant-container")
        assertElementAttribute(
            "#btn-whatstapp",
            "href",
            "https://wa.me/23799999999?text=Hi%21+I%27m+interested+in+this+product.%0Ahttp%3A%2F%2Flocalhost%3A0%2Fproduct%2F100%2Fproduct-100"
        )
    }

    @Test
    fun loggedIn() {
        val user = setupLoggedInUser(userId = 333L)

        navigate(url(product.slug))
        assertCurrentPageIs(PageName.PRODUCT)

        // Tracking
        val track = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(track.capture())
        assertEquals(product.id.toString(), track.firstValue.productId.toString())
        assertEquals(PageName.PRODUCT, track.firstValue.page)
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
