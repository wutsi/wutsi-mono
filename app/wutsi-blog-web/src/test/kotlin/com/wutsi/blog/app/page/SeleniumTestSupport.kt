package com.wutsi.blog.app.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.account.dto.GetSessionResponse
import com.wutsi.blog.account.dto.Session
import com.wutsi.blog.ads.dto.SearchAdsResponse
import com.wutsi.blog.app.backend.AdsBackend
import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.backend.BookBackend
import com.wutsi.blog.app.backend.CategoryBackend
import com.wutsi.blog.app.backend.CommentBackend
import com.wutsi.blog.app.backend.DiscountBackend
import com.wutsi.blog.app.backend.IpApiBackend
import com.wutsi.blog.app.backend.KpiBackend
import com.wutsi.blog.app.backend.LikeBackend
import com.wutsi.blog.app.backend.OfferBackend
import com.wutsi.blog.app.backend.PinBackend
import com.wutsi.blog.app.backend.ProductBackend
import com.wutsi.blog.app.backend.ReaderBackend
import com.wutsi.blog.app.backend.ShareBackend
import com.wutsi.blog.app.backend.StoreBackend
import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.backend.SubscriptionBackend
import com.wutsi.blog.app.backend.TagBackend
import com.wutsi.blog.app.backend.TopicBackend
import com.wutsi.blog.app.backend.TrackingBackend
import com.wutsi.blog.app.backend.TransactionBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.backend.WalletBackend
import com.wutsi.blog.app.backend.dto.IpApiResponse
import com.wutsi.blog.app.config.SecurityConfiguration
import com.wutsi.blog.app.security.QASecurityConfiguration
import com.wutsi.blog.app.service.AccessTokenStorage
import com.wutsi.blog.comment.dto.SearchCommentResponse
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchStoryKpiResponse
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiResponse
import com.wutsi.blog.product.dto.GetStoreResponse
import com.wutsi.blog.product.dto.SearchBookResponse
import com.wutsi.blog.product.dto.SearchCategoryResponse
import com.wutsi.blog.product.dto.SearchDiscountResponse
import com.wutsi.blog.product.dto.SearchOfferResponse
import com.wutsi.blog.product.dto.SearchProductResponse
import com.wutsi.blog.product.dto.Store
import com.wutsi.blog.story.dto.RecommendStoryResponse
import com.wutsi.blog.story.dto.SearchReaderResponse
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.WPPConfig
import com.wutsi.blog.subscription.dto.SearchSubscriptionResponse
import com.wutsi.blog.transaction.dto.GetWalletResponse
import com.wutsi.blog.transaction.dto.PaymentMethodType
import com.wutsi.blog.transaction.dto.SearchTransactionResponse
import com.wutsi.blog.transaction.dto.Wallet
import com.wutsi.blog.transaction.dto.WalletAccount
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.RecommendUserResponse
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.User
import com.wutsi.platform.core.storage.StorageService
import feign.FeignException
import feign.Request
import feign.RequestTemplate
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.Select
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import java.nio.charset.Charset
import java.util.Date
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("qa")
abstract class SeleniumTestSupport {
    @LocalServerPort
    protected val port: Int = 0

    protected var url: String = ""

    protected var timeout = 2L

    protected lateinit var driver: WebDriver

    @MockBean
    protected lateinit var storage: StorageService

    @MockBean
    protected lateinit var userBackend: UserBackend

    @MockBean
    protected lateinit var storyBackend: StoryBackend

    @MockBean
    protected lateinit var subscriptionBackend: SubscriptionBackend

    @MockBean
    protected lateinit var authenticationBackend: AuthenticationBackend

    @MockBean
    protected lateinit var accessTokenStorage: AccessTokenStorage

    @MockBean
    protected lateinit var walletBackend: WalletBackend

    @MockBean
    protected lateinit var likeBackend: LikeBackend

    @MockBean
    protected lateinit var shareBackend: ShareBackend

    @MockBean
    protected lateinit var commentBackend: CommentBackend

    @MockBean
    protected lateinit var trackingBackend: TrackingBackend

    @MockBean
    protected lateinit var topicBackend: TopicBackend

    @MockBean
    protected lateinit var ipApiBackend: IpApiBackend

    @MockBean
    protected lateinit var kpiBackend: KpiBackend

    @MockBean
    protected lateinit var tagBackend: TagBackend

    @MockBean
    protected lateinit var transactionBackend: TransactionBackend

    @MockBean
    protected lateinit var pinBackend: PinBackend

    @MockBean
    protected lateinit var readerBackend: ReaderBackend

    @MockBean
    protected lateinit var storeBackend: StoreBackend

    @MockBean
    protected lateinit var productBackend: ProductBackend

    @MockBean
    protected lateinit var discountBackend: DiscountBackend

    @MockBean
    protected lateinit var offerBackend: OfferBackend

    @MockBean
    protected lateinit var bookBackend: BookBackend

    @MockBean
    protected lateinit var categoryBackend: CategoryBackend

    @MockBean
    protected lateinit var adsBackend: AdsBackend

    protected fun setupLoggedInUser(
        userId: Long,
        userName: String = "ray.sponsible",
        fullName: String = "Ray Sponsible",
        biography: String? = "This is an example of bio",
        email: String? = "ray.sponsible@gmail.com",
        pictureUrl: String? = "https://picsum.photos/200/200",
        blog: Boolean = false,
        walletId: String? = null,
        superUser: Boolean = false,
        accountNumber: String = "+23799505677",
        accountOwner: String = "Ray Sponsible",
        wpp: Boolean = false,
        language: String = "en",
        subscriberCount: Int = WPPConfig.MIN_SUBSCRIBER_COUNT * 2,
        publishStoryCount: Int = WPPConfig.MIN_STORY_COUNT * 2,
        creationDateTime: Date = DateUtils.addMonths(Date(), -WPPConfig.MIN_AGE_MONTHS * 2),
        storeId: String? = null,
    ): User {
        val accessToken = UUID.randomUUID().toString()
        doReturn(accessToken).whenever(accessTokenStorage).get(any())

        doReturn(
            GetSessionResponse(
                Session(
                    accessToken = accessToken,
                    userId = userId,
                    accountId = userId * 10,
                    loginDateTime = Date(),
                ),
            ),
        ).whenever(authenticationBackend).session(any())

        val user = User(
            id = userId,
            name = userName,
            email = email,
            pictureUrl = pictureUrl,
            blog = blog,
            biography = biography,
            websiteUrl = "https://www.google.ca",
            facebookId = "ray-sponsible",
            youtubeId = "ray.sponsible",
            whatsappId = "4309430943",
            telegramId = "509504",
            githubId = "44444",
            walletId = walletId,
            fullName = fullName,
            readCount = 1000,
            publishStoryCount = publishStoryCount.toLong(),
            subscriberCount = subscriberCount.toLong(),
            superUser = superUser,
            wpp = wpp,
            language = language,
            creationDateTime = creationDateTime,
            storeId = storeId,
            country = "CM",
        )
        doReturn(GetUserResponse(user)).whenever(userBackend).get(userId)
        doReturn(GetUserResponse(user)).whenever(userBackend).get(userName)

        if (walletId != null) {
            val wallet = Wallet(
                id = walletId,
                balance = 150000,
                currency = "XAF",
                country = "CM",
                userId = userId,
                donationCount = 5,
                account = WalletAccount(
                    number = accountNumber,
                    PaymentMethodType.MOBILE_MONEY,
                    owner = accountOwner,
                ),
            )
            doReturn(GetWalletResponse(wallet)).whenever(walletBackend).get(walletId)
        }

        if (storeId != null) {
            val store = Store(
                id = storeId,
                userId = userId,
                currency = "XAF",
                totalSales = 50000,
                orderCount = 3,
            )
            doReturn(GetStoreResponse(store)).whenever(storeBackend).get(storeId)
        }
        login()
        return user
    }

    private fun login() {
        val state = UUID.randomUUID().toString()
        driver.get(
            url + QASecurityConfiguration.QA_SIGNIN_PATTERN + "?" + SecurityConfiguration.PARAM_STATE + "=$state",
        )
    }

    @BeforeEach
    fun setUp() {
        this.url = "http://localhost:$port"

        setupSelenium()
        setupDefaultApiResponses()
    }

    private fun setupSelenium() {
        val options = ChromeOptions()
        options.addArguments("--disable-web-security") // To prevent CORS issues
        options.addArguments("--lang=en")
        options.addArguments("--allowed-ips=")
        options.addArguments("--remote-allow-origins=*")
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246")
        if (System.getProperty("headless") == "true") {
            options.addArguments("--headless")
            options.addArguments("--no-sandbox")
            options.addArguments("--disable-dev-shm-usage")
        }

        this.driver = ChromeDriver(options)
        if (System.getProperty("headless") == "true") { // In headless mode, set a size that will not require vertical scrolling
            driver.manage().window().size = Dimension(1920, 1280)
        }
    }

    private fun setupDefaultApiResponses() {
        doReturn(SearchUserResponse()).whenever(userBackend).search(any())
        doReturn(RecommendUserResponse()).whenever(userBackend).recommend(any())
        doReturn(SearchStoryResponse()).whenever(storyBackend).search(any())
        doReturn(RecommendStoryResponse()).whenever(storyBackend).recommend(any())
        doReturn(SearchSubscriptionResponse()).whenever(subscriptionBackend).search(any())
        doReturn(SearchUserKpiResponse()).whenever(kpiBackend).search(any<SearchUserKpiRequest>())
        doReturn(SearchStoryKpiResponse()).whenever(kpiBackend).search(any<SearchStoryKpiRequest>())
        doReturn(SearchReaderResponse()).whenever(readerBackend).search(any())
        doReturn(IpApiResponse(countryCode = "CM")).whenever(ipApiBackend).resolve(any())
        doReturn(SearchCommentResponse()).whenever(commentBackend).search(any())
        doReturn(SearchProductResponse()).whenever(productBackend).search(any())
        doReturn(SearchDiscountResponse()).whenever(discountBackend).search(any())
        doReturn(SearchOfferResponse()).whenever(offerBackend).search(any())
        doReturn(SearchTransactionResponse()).whenever(transactionBackend).search(any())
        doReturn(SearchBookResponse()).whenever(bookBackend).search(any())
        doReturn(SearchCategoryResponse()).whenever(categoryBackend).search(any())
        doReturn(SearchAdsResponse()).whenever(adsBackend).search(any())
    }

    @AfterEach
    @Throws(Exception::class)
    fun tearDown() {
        driver.quit()
    }

    protected fun navigate(url: String) {
        driver.get(url)
    }

    protected fun url(path: String): String =
        if (path.startsWith("/")) {
            "http://localhost:$port$path"
        } else {
            "http://localhost:$port/$path"
        }

    protected fun assertCurrentPageIs(page: String) {
        assertEquals(page, driver.findElement(By.cssSelector("meta[name=wutsi\\:page_name]"))?.getAttribute("content"))
    }

    protected fun assertElementNotPresent(selector: String) {
        assertTrue(driver.findElements(By.cssSelector(selector)).size == 0)
    }

    protected fun assertElementPresent(selector: String) {
        assertTrue(driver.findElements(By.cssSelector(selector)).size > 0)
    }

    protected fun assertElementText(selector: String, text: String?) {
        assertEquals(text, driver.findElement(By.cssSelector(selector)).text)
    }

    protected fun assertElementTextContains(selector: String, text: String) {
        assertTrue(driver.findElement(By.cssSelector(selector)).text.contains(text))
    }

    protected fun assertElementCount(selector: String, count: Int) {
        assertEquals(count, driver.findElements(By.cssSelector(selector)).size)
    }

    protected fun assertElementNotVisible(selector: String) {
        assertEquals("none", driver.findElement(By.cssSelector(selector)).getCssValue("display"))
    }

    protected fun assertElementVisible(selector: String) {
        assertFalse("none".equals(driver.findElement(By.cssSelector(selector)).getCssValue("display")))
    }

    protected fun assertElementAttribute(selector: String, name: String, value: String?) {
        if (value == null) {
            assertNull(driver.findElement(By.cssSelector(selector)).getAttribute(name))
        } else {
            assertEquals(value, driver.findElement(By.cssSelector(selector)).getAttribute(name))
        }
    }

    protected fun assertElementAttributeStartsWith(selector: String, name: String, value: String) {
        assertTrue(driver.findElement(By.cssSelector(selector)).getAttribute(name).startsWith(value))
    }

    protected fun assertElementAttributeEndsWith(selector: String, name: String, value: String) {
        assertTrue(driver.findElement(By.cssSelector(selector)).getAttribute(name).endsWith(value))
    }

    protected fun assertElementAttributeContains(selector: String, name: String, value: String) {
        assertTrue(driver.findElement(By.cssSelector(selector)).getAttribute(name).contains(value))
    }

    protected fun assertElementHasClass(selector: String, value: String) {
        assertTrue(driver.findElement(By.cssSelector(selector)).getAttribute("class").contains(value))
    }

    protected fun assertElementHasNotClass(selector: String, value: String) {
        assertFalse(driver.findElement(By.cssSelector(selector)).getAttribute("class").contains(value))
    }

    protected fun click(selector: String, delayMillis: Long? = null) {
        driver.findElement(By.cssSelector(selector)).click()
        delayMillis?.let { Thread.sleep(delayMillis) }
    }

    protected fun scrollToBottom() {
        val js = driver as JavascriptExecutor
        js.executeScript("window.scrollBy(0,document.body.scrollHeight)")
        Thread.sleep(1000)
    }

    protected fun scrollToMiddle() {
        val js = driver as JavascriptExecutor
        js.executeScript("window.scrollBy(0,document.body.scrollHeight/2)")
        Thread.sleep(1000)
    }

    protected fun scroll(percent: Double) {
        val js = driver as JavascriptExecutor
        js.executeScript("window.scrollBy(0,document.body.scrollHeight*$percent)")
        Thread.sleep(1000)
    }

    protected fun input(selector: String, value: String) {
        val by = By.cssSelector(selector)
        driver.findElement(by).clear()
        driver.findElement(by).sendKeys(value)
    }

    protected fun select(selector: String, index: Int) {
        val by = By.cssSelector(selector)
        val select = Select(driver.findElement(by))
        select.selectByIndex(index)
    }

    protected fun assertAppStoreLinksPresent() {
        assertElementAttribute(
            ".cta-android",
            "href",
            "https://play.google.com/store/apps/details?id=com.wutsi.wutsi_wallet",
        )
        assertElementNotPresent(".cta-ios")
    }

    protected fun createFeignConflictException(
        errorCode: String,
    ) = FeignException.Conflict(
        "",
        Request.create(
            Request.HttpMethod.POST,
            "https://www.google.ca",
            emptyMap(),
            "".toByteArray(),
            Charset.defaultCharset(),
            RequestTemplate(),
        ),
        """
            {
                "error":{
                    "code": "$errorCode"
                }
            }
        """.trimIndent().toByteArray(),
        emptyMap(),
    )

    protected fun createFeignNotFoundException(
        errorCode: String,
    ) = FeignException.NotFound(
        "",
        Request.create(
            Request.HttpMethod.POST,
            "https://www.google.ca",
            emptyMap(),
            "".toByteArray(),
            Charset.defaultCharset(),
            RequestTemplate(),
        ),
        """
            {
                "error":{
                    "code": "$errorCode"
                }
            }
        """.trimIndent().toByteArray(),
        emptyMap(),
    )
}
