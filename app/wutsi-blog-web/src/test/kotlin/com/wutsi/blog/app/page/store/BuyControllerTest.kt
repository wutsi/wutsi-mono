package com.wutsi.blog.app.page.store

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.payment.DonateControllerTest
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.GetProductResponse
import com.wutsi.blog.product.dto.GetStoreResponse
import com.wutsi.blog.product.dto.Product
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.Store
import com.wutsi.blog.transaction.dto.GetTransactionResponse
import com.wutsi.blog.transaction.dto.GetWalletResponse
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.SubmitChargeCommand
import com.wutsi.blog.transaction.dto.SubmitChargeResponse
import com.wutsi.blog.transaction.dto.Transaction
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.transaction.dto.Wallet
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.User
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Status
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class BuyControllerTest : SeleniumTestSupport() {
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

    private val wallet = Wallet(
        id = DonateControllerTest.WALLET_ID,
        balance = 1000,
        currency = "XAF",
        userId = DonateControllerTest.BLOG_ID,
        country = "CM",
    )

    private val transactionId = UUID.randomUUID().toString()

    override fun setUp() {
        super.setUp()

        doReturn(GetProductResponse(product)).whenever(productBackend).get(any())

        doReturn(GetStoreResponse(store)).whenever(storeBackend).get(any())

        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.id)
        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.name)

        doReturn(GetWalletResponse(wallet)).whenever(walletBackend).get(any())
    }

    @Test
    fun success() {
        navigate(url("/product/${product.id}"))
        assertCurrentPageIs(PageName.SHOP_PRODUCT)

        click("#btn-buy")
        assertCurrentPageIs(PageName.SHOP_BUY)
        input("#full-name", "Ray Sponsible")
        input("#email", "ray.sponsible@gmail.com")
        input("#phone-number", "99999999")

        doReturn(
            SubmitChargeResponse(transactionId = transactionId, status = Status.PENDING.name),
        ).whenever(transactionBackend).charge(any())

        doReturn(
            GetTransactionResponse(
                transaction = Transaction(
                    id = transactionId,
                    status = Status.PENDING,
                    productId = product.id,
                    storeId = store.id,
                    type = TransactionType.CHARGE,
                )
            ),
        ).doReturn(
            GetTransactionResponse(
                transaction = Transaction(
                    id = transactionId,
                    status = Status.SUCCESSFUL,
                    productId = product.id,
                    storeId = store.id,
                    type = TransactionType.CHARGE,
                )
            ),
        ).whenever(transactionBackend).get(any(), any())

        click("#btn-submit", 1000)
        assertCurrentPageIs(PageName.DONATE_PROCESSING)

        val cmd = argumentCaptor<SubmitChargeCommand>()
        verify(transactionBackend).charge(cmd.capture())
        assertEquals(product.id, cmd.firstValue.productId)
        assertEquals("XAF", cmd.firstValue.currency)
        assertEquals("ray.sponsible@gmail.com", cmd.firstValue.email)
        assertEquals("+23799999999", cmd.firstValue.paymentNumber)
        assertEquals("Ray Sponsible", cmd.firstValue.paymentMethodOwner)
        assertEquals(product.price, cmd.firstValue.amount)
        assertEquals(PaymentMethodType.MOBILE_MONEY, cmd.firstValue.paymentMethodType)

        assertElementVisible("#processing-container")
        assertElementNotVisible("#success-container")
        assertElementNotVisible("#failed-container")
        assertElementNotVisible("#expired-container")

        Thread.sleep(15000)
        assertElementNotVisible("#processing-container")
        assertElementVisible("#success-container")
        assertElementNotVisible("#failed-container")
        assertElementNotVisible("#expired-container")
        assertElementVisible("#btn-download")
        assertElementAttributeEndsWith("#btn-download", "href", "/product/${product.id}/download/$transactionId")

        click("#btn-continue")
        assertCurrentPageIs(PageName.SHOP)
    }

    @Test
    fun failure() {
        navigate(url("/product/${product.id}"))
        assertCurrentPageIs(PageName.SHOP_PRODUCT)

        click("#btn-buy")
        assertCurrentPageIs(PageName.SHOP_BUY)
        input("#full-name", "Ray Sponsible")
        input("#email", "ray.sponsible@gmail.com")
        input("#phone-number", "99999999")

        doReturn(
            SubmitChargeResponse(transactionId = transactionId, status = Status.PENDING.name),
        ).whenever(transactionBackend).charge(any())

        doReturn(
            GetTransactionResponse(
                transaction = Transaction(
                    status = Status.PENDING,
                    productId = product.id,
                    storeId = store.id,
                    type = TransactionType.CHARGE,
                )
            ),
        ).doReturn(
            GetTransactionResponse(
                transaction = Transaction(
                    status = Status.FAILED,
                    errorCode = ErrorCode.FRAUDULENT.name,
                    productId = product.id,
                    storeId = store.id,
                    type = TransactionType.CHARGE,
                )
            ),
        ).whenever(transactionBackend).get(any(), any())

        click("#btn-submit", 1000)
        assertCurrentPageIs(PageName.DONATE_PROCESSING)

        val cmd = argumentCaptor<SubmitChargeCommand>()
        verify(transactionBackend).charge(cmd.capture())
        assertEquals(product.id, cmd.firstValue.productId)
        assertEquals("XAF", cmd.firstValue.currency)
        assertEquals("ray.sponsible@gmail.com", cmd.firstValue.email)
        assertEquals("+23799999999", cmd.firstValue.paymentNumber)
        assertEquals("Ray Sponsible", cmd.firstValue.paymentMethodOwner)
        assertEquals(product.price, cmd.firstValue.amount)
        assertEquals(PaymentMethodType.MOBILE_MONEY, cmd.firstValue.paymentMethodType)

        assertElementVisible("#processing-container")
        assertElementNotVisible("#success-container")
        assertElementNotVisible("#failed-container")
        assertElementNotVisible("#expired-container")

        Thread.sleep(15000)
        assertElementNotVisible("#processing-container")
        assertElementNotVisible("#success-container")
        assertElementVisible("#failed-container")
        assertElementNotVisible("#expired-container")

        click("#btn-try-again")
        assertCurrentPageIs(PageName.SHOP_BUY)
    }
}