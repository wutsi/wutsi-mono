package com.wutsi.blog.app.page.store

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.GetStoreResponse
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.blog.product.dto.SearchProductResponse
import com.wutsi.blog.product.dto.Store
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.User
import org.junit.jupiter.api.Test
import java.net.URL
import javax.imageio.ImageIO
import kotlin.test.assertEquals

class ShopControllerTest : SeleniumTestSupport() {
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
            userId = BLOG_ID,
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
            userId = BLOG_ID,
            price = 1000,
            currency = "XAF",
            status = ProductStatus.PUBLISHED,
            available = true,
            slug = "/product/200/product-200",
        )
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
        userId = BLOG_ID,
        currency = "XAF",
    )

    override fun setUp() {
        super.setUp()

        doReturn(SearchProductResponse(products)).whenever(productBackend).search(any())

        doReturn(GetStoreResponse(store)).whenever(storeBackend).get(any())

        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.id)
        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.name)
    }

    @Test
    fun browse() {
        navigate(url("/@/${blog.name}/shop"))

        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttributeEndsWith("head meta[property='og:url']", "content", "/@/${blog.name}/shop")
        assertElementAttribute(
            "head meta[property='og:image']",
            "content",
            "http://localhost:0/@/${blog.name}/shop.png",
        )

        assertCurrentPageIs(PageName.SHOP)
        assertElementCount(".product-card", products.size)
    }

    @Test
    fun noStore() {
        val xblog = blog.copy(storeId = null)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.name)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.id)

        navigate(url("/@/${blog.name}/shop"))

        assertCurrentPageIs(PageName.ERROR)
    }

    @Test
    fun notABlog() {
        val xblog = blog.copy(blog = false)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.name)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.id)

        navigate(url("/@/${blog.name}/shop"))

        assertCurrentPageIs(PageName.ERROR)
    }

    @Test
    fun image() {
        val img = ImageIO.read(URL("http://localhost:$port/@/${blog.name}/shop.png"))

        assertEquals(1200, img.width)
        assertEquals(630, img.height)
    }
}
