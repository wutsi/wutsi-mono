package com.wutsi.blog.app.page.store

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.payment.DonateControllerTest
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.dto.GetProductResponse
import com.wutsi.blog.product.dto.GetStoreResponse
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.dto.Product
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchOfferResponse
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
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull

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

    private val offer = Offer(
        productId = product.id,
        price = 800,
        referencePrice = 1000,
        savingAmount = 200,
        savingPercentage = 20,
        discount = Discount(
            type = DiscountType.SUBSCRIBER,
            percentage = 20,
            expiryDate = DateUtils.addDays(Date(), 1),
        ),
        internationalPrice = 2,
        internationalCurrency = "EUR"
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

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetProductResponse(product)).whenever(productBackend).get(any())
        doReturn(SearchOfferResponse(listOf(offer))).whenever(offerBackend).search(any())

        doReturn(GetStoreResponse(store)).whenever(storeBackend).get(any())

        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.id)
        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.name)

        doReturn(GetWalletResponse(wallet)).whenever(walletBackend).get(any())
    }

    @Test
    fun success() {
        navigate(url("/product/${product.id}"))
        assertCurrentPageIs(PageName.PRODUCT)

        click("#btn-buy")
        assertCurrentPageIs(PageName.BUY)
        assertElementPresent("#paypal-container")
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
        assertCurrentPageIs(PageName.PROCESSING)

        val cmd = argumentCaptor<SubmitChargeCommand>()
        verify(transactionBackend).charge(cmd.capture())
        assertEquals(product.id, cmd.firstValue.productId)
        assertEquals("XAF", cmd.firstValue.currency)
        assertEquals("ray.sponsible@gmail.com", cmd.firstValue.email)
        assertEquals("+23799999999", cmd.firstValue.paymentNumber)
        assertEquals("Ray Sponsible", cmd.firstValue.paymentMethodOwner)
        assertEquals(offer.price, cmd.firstValue.amount)
        assertEquals(PaymentMethodType.MOBILE_MONEY, cmd.firstValue.paymentMethodType)
        assertEquals(DiscountType.SUBSCRIBER, cmd.firstValue.discountType)
        assertNull(cmd.firstValue.userId)
        assertNull(cmd.firstValue.internationalCurrency)

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
    fun free() {
        val me = setupLoggedInUser(555)
        doReturn(
            SearchOfferResponse(
                listOf(
                    offer.copy(
                        savingPercentage = 100,
                        price = 0,
                        referencePrice = product.price,
                        discount = offer.discount?.copy(type = DiscountType.DONATION)
                    )
                )
            )
        ).whenever(offerBackend).search(any())

        navigate(url("/product/${product.id}"))
        assertCurrentPageIs(PageName.PRODUCT)

        click("#btn-buy")
        assertCurrentPageIs(PageName.BUY)
        assertElementNotPresent("#paypal-container")
        assertElementNotPresent("#phone-number")
        input("#full-name", "Ray Sponsible")
        input("#email", "ray.sponsible@gmail.com")

        doReturn(
            SubmitChargeResponse(transactionId = transactionId, status = Status.PENDING.name),
        ).whenever(transactionBackend).charge(any())

        doReturn(
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
        assertCurrentPageIs(PageName.PROCESSING)

        val cmd = argumentCaptor<SubmitChargeCommand>()
        verify(transactionBackend).charge(cmd.capture())
        assertEquals(product.id, cmd.firstValue.productId)
        assertEquals("XAF", cmd.firstValue.currency)
        assertEquals(me.email, cmd.firstValue.email)
        assertEquals("", cmd.firstValue.paymentNumber)
        assertEquals(me.fullName, cmd.firstValue.paymentMethodOwner)
        assertEquals(0L, cmd.firstValue.amount)
        assertEquals(PaymentMethodType.NONE, cmd.firstValue.paymentMethodType)
        assertEquals(DiscountType.DONATION, cmd.firstValue.discountType)
        assertEquals(me.id, cmd.firstValue.userId)

        Thread.sleep(5000)
        assertElementVisible("#btn-download")
        assertElementAttributeEndsWith("#btn-download", "href", "/product/${product.id}/download/$transactionId")

        click("#btn-continue")
        assertCurrentPageIs(PageName.SHOP)
    }

    @Test
    fun failure() {
        navigate(url("/product/${product.id}"))
        assertCurrentPageIs(PageName.PRODUCT)

        click("#btn-buy")
        assertCurrentPageIs(PageName.BUY)
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
        assertCurrentPageIs(PageName.PROCESSING)

        val cmd = argumentCaptor<SubmitChargeCommand>()
        verify(transactionBackend).charge(cmd.capture())
        assertEquals(product.id, cmd.firstValue.productId)
        assertEquals("XAF", cmd.firstValue.currency)
        assertEquals("ray.sponsible@gmail.com", cmd.firstValue.email)
        assertEquals("+23799999999", cmd.firstValue.paymentNumber)
        assertEquals("Ray Sponsible", cmd.firstValue.paymentMethodOwner)
        assertEquals(offer.price, cmd.firstValue.amount)
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
        assertCurrentPageIs(PageName.BUY)
    }

    @Test
    fun abandoned() {
        val tx = Transaction(
            id = "111111",
            email = "roger.milla@gmail.com",
            paymentMethodOwner = "Roger Milla"
        )
        doReturn(GetTransactionResponse(tx)).whenever(transactionBackend).get(tx.id, false)

        navigate(url("/buy?product-id=${product.id}&t=${tx.id}"))
        assertCurrentPageIs(PageName.BUY)

        assertElementAttribute("#full-name", "value", tx.paymentMethodOwner)
        assertElementAttribute("#email", "value", tx.email)
    }

    @Test
    fun abandonedWithInvalidTransaction() {
        doThrow(RuntimeException::class).whenever(transactionBackend).get(any(), any())

        navigate(url("/buy?product-id=${product.id}&t=1111"))
        assertCurrentPageIs(PageName.BUY)

        assertElementAttribute("#full-name", "value", "")
        assertElementAttribute("#email", "value", "")
    }
}
