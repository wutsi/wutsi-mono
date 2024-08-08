package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.store.ShopControllerTest
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.BookSummary
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.dto.GetStoreResponse
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.blog.product.dto.SearchBookResponse
import com.wutsi.blog.product.dto.SearchOfferResponse
import com.wutsi.blog.product.dto.SearchProductResponse
import com.wutsi.blog.product.dto.Store
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class LibraryControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 1L
        const val USER_ID = 555L
        const val STORE_ID = "100"
    }

    private val products = listOf(
        ProductSummary(
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
        ),
        ProductSummary(
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
    )

    private val offers = listOf(
        Offer(
            productId = 100,
            price = 800,
            referencePrice = 1000,
            savingAmount = 200,
            savingPercentage = 20,
            discount = Discount(
                type = DiscountType.SUBSCRIBER,
                percentage = 20
            )
        ),
        Offer(
            productId = 200,
            price = 1200,
            referencePrice = 1500,
            savingAmount = 300,
            savingPercentage = 20,
            discount = Discount(
                type = DiscountType.SUBSCRIBER,
                percentage = 20
            )
        )
    )

    private val books = listOf(
        BookSummary(
            id = 100,
            userId = USER_ID,
            product = ProductSummary(
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
                storeId = STORE_ID,
                price = 1500,
                currency = "XAF",
                status = ProductStatus.PUBLISHED,
                available = true,
                slug = "/product/200/product-200",
            )
        ),
        BookSummary(
            id = 300,
            userId = USER_ID,
            product = ProductSummary(
                id = 200,
                title = "EXPIRED Product 300",
                imageUrl = "https://picsum.photos/1200/600",
                fileUrl = "https://www.google.ca/123.pdf",
                storeId = STORE_ID,
                price = 1500,
                currency = "XAF",
                status = ProductStatus.PUBLISHED,
                available = true,
                slug = "/product/200/product-300",
            ),
            expiryDate = Date(System.currentTimeMillis() - 10000)
        ),
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
        country = "CM",
    )

    private val store = Store(
        id = STORE_ID,
        userId = BLOG_ID,
        currency = "XAF",
        subscriberDiscount = 20,
        nextPurchaseDiscount = 30,
        firstPurchaseDiscount = 10,
        nextPurchaseDiscountDays = 14,
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchBookResponse(books)).whenever(bookBackend).search(any())

        doReturn(GetStoreResponse(store)).whenever(storeBackend).get(STORE_ID)

        doReturn(GetUserResponse(blog)).whenever(userBackend).get(BLOG_ID)

        doReturn(SearchProductResponse(products))
            .doReturn(SearchProductResponse(products.map { it.copy(storeId = "xxx") }))
            .whenever(productBackend).search(any())
        doReturn(SearchOfferResponse(offers)).whenever(offerBackend).search(any())
    }

    @Test
    fun index() {
        setupLoggedInUser(userId = USER_ID)
        navigate(url("/me/library"))

        assertCurrentPageIs(PageName.LIBRARY)
        assertElementCount(".book-card", 2)

        Thread.sleep(5000)

        assertElementCount("#library-store-100 .product-card", 2)
        assertElementCount("#library-other-stores .product-card", 2)
    }

    @Test
    fun requiresLogin() {
        navigate(url("/me/library"))

        assertCurrentPageIs(PageName.LOGIN)
    }
}
