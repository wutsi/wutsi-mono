package com.wutsi.blog.app.page.payment

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.reader.ReadControllerTest
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.product.dto.GetStoreResponse
import com.wutsi.blog.product.dto.Store
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.Story
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.Tag
import com.wutsi.blog.story.dto.Topic
import com.wutsi.blog.transaction.dto.GetTransactionResponse
import com.wutsi.blog.transaction.dto.GetWalletResponse
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.SubmitDonationCommand
import com.wutsi.blog.transaction.dto.SubmitDonationResponse
import com.wutsi.blog.transaction.dto.Transaction
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.transaction.dto.Wallet
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.User
import com.wutsi.platform.payment.core.ErrorCode
import com.wutsi.platform.payment.core.Status
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.test.assertEquals

class DonateControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 1L
        const val WALLET_ID = "1111"
    }

    private val wallet = Wallet(
        id = WALLET_ID,
        balance = 1000,
        currency = "XAF",
        userId = BLOG_ID,
        country = "CM",
    )

    private val blog = User(
        id = BLOG_ID,
        blog = true,
        name = "test",
        fullName = "Test Blog",
        walletId = WALLET_ID,
    )

    private val transactionId = UUID.randomUUID().toString()

    private val story = Story(
        id = ReadControllerTest.STORY_ID,
        userId = ReadControllerTest.BLOG_ID,
        title = "Ukraine: Finalement la paix! Poutine et Zelynski font un calin",
        tagline = "Il etait temps!!!",
        content = IOUtils.toString(ReadControllerTest::class.java.getResourceAsStream("/story.json")),
        slug = "/read/${ReadControllerTest.STORY_ID}/ukraine-finalement-la-paix",
        thumbnailUrl = "https://picsum.photos/1200/800",
        language = "en",
        summary = "This is the summary of the story",
        tags = listOf("Ukraine", "Russie", "Poutine", "Zelynsky", "Guerre").map { Tag(name = it) },
        creationDateTime = Date(),
        modificationDateTime = Date(),
        status = StoryStatus.PUBLISHED,
        topic = Topic(
            id = 100,
            name = "Topic 100",
        ),
        likeCount = 10,
        liked = false,
        commentCount = 300,
        shareCount = 3500,
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetUserResponse(blog)).whenever(userBackend).get(BLOG_ID)
        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.name)

        doReturn(GetWalletResponse(wallet)).whenever(walletBackend).get(any())
    }

    @Test
    fun `successful donation`() {
        navigate("$url/@/${blog.name}/donate")
        assertCurrentPageIs(PageName.DONATE)

        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttributeEndsWith("head meta[property='og:url']", "content", "/@/${blog.name}/donate")
        assertElementAttribute(
            "head meta[property='og:image']",
            "content",
            "http://localhost:0/@/${blog.name}/donate.png",
        )

        click("#btn-donate-3")
        input("#email", "ray.sponsible@gmail.com")
        input("#phone-number", "99999999")
        input("#full-name", "Ray Sponsible")

        doReturn(
            SubmitDonationResponse(
                transactionId = transactionId,
                status = Status.PENDING.name,
            ),
        ).whenever(transactionBackend).donate(any())

        doReturn(
            GetTransactionResponse(transaction = Transaction(status = Status.PENDING, type = TransactionType.DONATION)),
        ).doReturn(
            GetTransactionResponse(
                transaction = Transaction(
                    status = Status.SUCCESSFUL,
                    type = TransactionType.DONATION
                )
            ),
        ).whenever(transactionBackend).get(any(), any())

        click("#btn-submit", 1000)
        assertCurrentPageIs(PageName.DONATE_PROCESSING)

        val cmd = argumentCaptor<SubmitDonationCommand>()
        verify(transactionBackend).donate(cmd.capture())
        assertEquals(wallet.id, cmd.firstValue.walletId)
        assertEquals("XAF", cmd.firstValue.currency)
        assertEquals("ray.sponsible@gmail.com", cmd.firstValue.email)
        assertEquals("+23799999999", cmd.firstValue.paymentNumber)
        assertEquals("Ray Sponsible", cmd.firstValue.paymentMethodOwner)
        assertEquals(Country.CM.defaultDonationAmounts[2], cmd.firstValue.amount)
        assertEquals(PaymentMethodType.MOBILE_MONEY, cmd.firstValue.paymentMethodType)

        assertElementVisible("#processing-container")
        assertElementNotVisible("#success-container")
        assertElementNotVisible("#failed-container")
        assertElementNotVisible("#expired-container")

        Thread.sleep(10000)
        assertElementNotVisible("#processing-container")
        assertElementVisible("#success-container")
        assertElementNotVisible("#failed-container")
        assertElementNotVisible("#expired-container")

        click("#btn-continue")
        assertCurrentPageIs(PageName.BLOG)
    }

    @Test
    fun `donation behind login`() {
        navigate(url("/me/donate/${blog.id}?redirect=/read/12232/this-is-nice"))
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `successful donation and redirect`() {
        val store = Store(id = "111", enableDonationDiscount = true, userId = blog.id)
        doReturn(GetStoreResponse(store)).whenever(storeBackend).get(any())

        val xblog = blog.copy(storeId = store.id)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(BLOG_ID)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(blog.name)

        val user = setupLoggedInUser(
            userId = 111,
            userName = "roger.milla",
            fullName = "Roger Milla",
            email = "roger.milla@gmail.com",
        )
        doReturn(GetStoryResponse(story)).whenever(storyBackend).get(any())

        navigate(url("/me/donate/${blog.id}?redirect=/read/12232/this-is-nice"))
        assertCurrentPageIs(PageName.DONATE)

        click("#btn-donate-1")
        input("#phone-number", "99999999")

        doReturn(
            SubmitDonationResponse(
                transactionId = transactionId,
                status = Status.PENDING.name,
            ),
        ).whenever(transactionBackend).donate(any())

        doReturn(
            GetTransactionResponse(transaction = Transaction(status = Status.PENDING, type = TransactionType.DONATION)),
        ).doReturn(
            GetTransactionResponse(
                transaction = Transaction(
                    status = Status.SUCCESSFUL,
                    type = TransactionType.DONATION
                )
            ),
        ).whenever(transactionBackend).get(any(), any())

        click("#btn-submit", 1000)
        assertCurrentPageIs(PageName.DONATE_PROCESSING)

        val cmd = argumentCaptor<SubmitDonationCommand>()
        verify(transactionBackend).donate(cmd.capture())
        assertEquals(wallet.id, cmd.firstValue.walletId)
        assertEquals("XAF", cmd.firstValue.currency)
        assertEquals(user.email, cmd.firstValue.email)
        assertEquals("+23799999999", cmd.firstValue.paymentNumber)
        assertEquals(user.fullName, cmd.firstValue.paymentMethodOwner)
        assertEquals(user.id, cmd.firstValue.userId)
        assertEquals(Country.CM.defaultDonationAmounts[0], cmd.firstValue.amount)
        assertEquals(PaymentMethodType.MOBILE_MONEY, cmd.firstValue.paymentMethodType)

        assertElementVisible("#processing-container")
        assertElementNotVisible("#success-container")
        assertElementNotVisible("#failed-container")
        assertElementNotVisible("#expired-container")

        Thread.sleep(10000)
        assertElementNotVisible("#processing-container")
        assertElementVisible("#success-container")
        assertElementNotVisible("#failed-container")
        assertElementNotVisible("#expired-container")

        click("#btn-continue")
//        assertCurrentPageIs(PageName.READ)
    }

    @Test
    fun `failed donation`() {
        navigate("$url/@/${blog.name}/donate")
        assertCurrentPageIs(PageName.DONATE)

        click("#btn-donate-1")
        input("#email", "ray.sponsible@gmail.com")
        input("#phone-number", "99999999")
        input("#full-name", "Ray Sponsible")

        doReturn(
            SubmitDonationResponse(
                transactionId = transactionId,
                status = Status.PENDING.name,
            ),
        ).whenever(transactionBackend).donate(any())

        doReturn(
            GetTransactionResponse(transaction = Transaction(status = Status.PENDING, type = TransactionType.DONATION)),
        ).doReturn(
            GetTransactionResponse(
                transaction = Transaction(
                    status = Status.FAILED,
                    errorCode = ErrorCode.FRAUDULENT.name,
                ),
            ),
        ).whenever(transactionBackend).get(any(), any())
        click("#btn-submit", 1000)
        assertCurrentPageIs(PageName.DONATE_PROCESSING)

        assertElementVisible("#processing-container")
        assertElementNotVisible("#success-container")
        assertElementNotVisible("#failed-container")
        assertElementNotVisible("#expired-container")

        Thread.sleep(10000)
        assertElementNotVisible("#processing-container")
        assertElementNotVisible("#success-container")
        assertElementVisible("#failed-container")
        assertElementNotVisible("#expired-container")
    }

    @Test
    fun `expired donation`() {
        navigate("$url/@/${blog.name}/donate")
        assertCurrentPageIs(PageName.DONATE)

        click("#btn-donate-4")
        input("#email", "ray.sponsible@gmail.com")
        input("#phone-number", "99999999")
        input("#full-name", "Ray Sponsible")

        doReturn(
            SubmitDonationResponse(
                transactionId = transactionId,
                status = Status.PENDING.name,
            ),
        ).whenever(transactionBackend).donate(any())

        doReturn(
            GetTransactionResponse(transaction = Transaction(status = Status.PENDING, type = TransactionType.DONATION)),
        ).whenever(transactionBackend).get(any(), any())
        click("#btn-submit", 1000)
        assertCurrentPageIs(PageName.DONATE_PROCESSING)

        assertElementVisible("#processing-container")
        assertElementNotVisible("#success-container")
        assertElementNotVisible("#failed-container")
        assertElementNotVisible("#expired-container")

        Thread.sleep(60000)
        assertElementNotVisible("#processing-container")
        assertElementNotVisible("#success-container")
        assertElementNotVisible("#failed-container")
        assertElementVisible("#expired-container")
    }

    @Test
    fun error() {
        navigate("$url/@/${blog.name}/donate")
        assertCurrentPageIs(PageName.DONATE)

        click("#btn-donate-3")
        input("#email", "ray.sponsible@gmail.com")
        input("#phone-number", "99999999")
        input("#full-name", "Ray Sponsible")

        doThrow(RuntimeException::class).whenever(transactionBackend).donate(any())

        click("#btn-submit", 1000)
        assertCurrentPageIs(PageName.DONATE)

        assertElementPresent(".alert-danger")
    }

    @Test
    fun image() {
        val img = ImageIO.read(URL("http://localhost:$port/@/${blog.name}/donate.png"))

        assertEquals(1200, img.width)
        assertEquals(630, img.height)
    }

    @Test
    fun `no image for user`() {
        val xblog = blog.copy(blog = false)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.name)

        val cnn = URL("http://localhost:$port/@/${blog.name}/donate.png").openConnection() as HttpURLConnection
        try {
            assertEquals(404, cnn.responseCode)
        } finally {
            cnn.disconnect()
        }
    }

    @Test
    fun `no image for wallet`() {
        val xblog = blog.copy(walletId = null)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.name)

        val cnn = URL("http://localhost:$port/@/${blog.name}/donate.png").openConnection() as HttpURLConnection
        try {
            assertEquals(404, cnn.responseCode)
        } finally {
            cnn.disconnect()
        }
    }

    @Test
    fun `user not blog`() {
        val xblog = blog.copy(blog = false)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(BLOG_ID)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.name)

        navigate("$url/@/${blog.name}/donate")
        assertCurrentPageIs(PageName.ERROR)
    }

    @Test
    fun `user without wallet`() {
        val xblog = blog.copy(walletId = null)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(BLOG_ID)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.name)

        navigate("$url/@/${blog.name}/donate")
        assertCurrentPageIs(PageName.ERROR)
    }

    @Test
    fun `invalid country`() {
        val xwallet = wallet.copy(country = "XXX")
        doReturn(GetWalletResponse(xwallet)).whenever(walletBackend).get(any())

        navigate("$url/@/${blog.name}/donate")
        assertCurrentPageIs(PageName.ERROR)
    }
}
