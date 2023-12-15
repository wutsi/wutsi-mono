package com.wutsi.blog.app.page.store

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.payment.DonateControllerTest
import com.wutsi.blog.product.dto.GetProductResponse
import com.wutsi.blog.product.dto.GetStoreResponse
import com.wutsi.blog.product.dto.Product
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.Store
import com.wutsi.blog.transaction.dto.GetTransactionResponse
import com.wutsi.blog.transaction.dto.GetWalletResponse
import com.wutsi.blog.transaction.dto.Transaction
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.transaction.dto.Wallet
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.User
import com.wutsi.platform.payment.core.Status
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import kotlin.test.assertEquals

class DownloadControllerTest : SeleniumTestSupport() {
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
        fileContentLength = 1,
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

    private val wallet = Wallet(
        id = DonateControllerTest.WALLET_ID,
        balance = 1000,
        currency = "XAF",
        userId = DonateControllerTest.BLOG_ID,
        country = "CM",
    )

    private val transaction = Transaction(
        id = UUID.randomUUID().toString(),
        type = TransactionType.CHARGE,
        productId = product.id,
        storeId = store.id,
        status = Status.SUCCESSFUL,
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetProductResponse(product)).whenever(productBackend).get(any())
        doReturn(GetStoreResponse(store)).whenever(storeBackend).get(any())
        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.id)
        doReturn(GetWalletResponse(wallet)).whenever(walletBackend).get(any())
        doReturn(GetTransactionResponse(transaction)).whenever(transactionBackend).get(any(), any())
    }

    @Test
    fun download() {
        // GIVEN
        val content = "1".repeat(product.fileContentLength.toInt())
        doAnswer { inv ->
            (inv.arguments[1] as OutputStream).write(content.toByteArray())
        }.whenever(storage).get(any(), any())

        // WHEN/THEN
        val url = "http://localhost:$port/product/${product.id}/download/${transaction.id}"
        val cnn = URL(url).openConnection() as HttpURLConnection
        try {
            assertEquals(200, cnn.responseCode)
            assertEquals(product.fileContentLength, cnn.contentLength.toLong())
            assertEquals(product.fileContentType, cnn.contentType)
            assertEquals(
                "attachment; filename=\"product-100.pdf\"",
                cnn.headerFields[HttpHeaders.CONTENT_DISPOSITION]?.get(0),
            )
        } finally {
            cnn.disconnect()
        }
    }

    @Test
    fun transactionPending() {
        // GIVEN
        val tx = transaction.copy(status = Status.PENDING)
        doReturn(GetTransactionResponse(tx)).whenever(transactionBackend).get(any(), any())

        // WHEN/THEN
        val url = "http://localhost:$port/product/${product.id}/download/${transaction.id}"
        val cnn = URL(url).openConnection() as HttpURLConnection
        try {
            assertEquals(404, cnn.responseCode)
        } finally {
            cnn.disconnect()
        }
    }

    @Test
    fun transactionFailed() {
        // GIVEN
        val tx = transaction.copy(status = Status.FAILED)
        doReturn(GetTransactionResponse(tx)).whenever(transactionBackend).get(any(), any())

        // WHEN/THEN
        val url = "http://localhost:$port/product/${product.id}/download/${transaction.id}"
        val cnn = URL(url).openConnection() as HttpURLConnection
        try {
            assertEquals(404, cnn.responseCode)
        } finally {
            cnn.disconnect()
        }
    }

    @Test
    fun noProduct() {
        // GIVEN
        val tx = transaction.copy(productId = null)
        doReturn(GetTransactionResponse(tx)).whenever(transactionBackend).get(any(), any())

        // WHEN/THEN
        val url = "http://localhost:$port/product/${product.id}/download/${transaction.id}"
        val cnn = URL(url).openConnection() as HttpURLConnection
        try {
            assertEquals(404, cnn.responseCode)
        } finally {
            cnn.disconnect()
        }
    }

    @Test
    fun transactionNotFound() {
        // GIVEN
        doThrow(createFeignNotFoundException("")).whenever(transactionBackend).get(any(), any())

        // WHEN/THEN
        val url = "http://localhost:$port/product/${product.id}/download/${transaction.id}"
        val cnn = URL(url).openConnection() as HttpURLConnection
        try {
            assertEquals(404, cnn.responseCode)
        } finally {
            cnn.disconnect()
        }
    }
}
