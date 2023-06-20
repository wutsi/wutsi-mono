package com.wutsi.blog.app.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.account.dto.Session
import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.backend.SubscriptionBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.service.AccessTokenStorage
import com.wutsi.blog.user.dto.User
import feign.FeignException
import feign.Request
import feign.RequestTemplate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.Select
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.nio.charset.Charset
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class SeleniumTestSupport {
    @LocalServerPort
    protected val port: Int = 0

    protected var url: String = ""

    protected var timeout = 2L

    protected lateinit var driver: WebDriver

    @MockBean
    protected lateinit var userBackend: UserBackend

    @MockBean
    protected lateinit var storyBackend: StoryBackend

    @MockBean
    protected lateinit var subscriptionBackend: SubscriptionBackend

    @MockBean
    protected lateinit var authBackend: AuthenticationBackend

    @MockBean
    protected lateinit var accessTokenStorage: AccessTokenStorage

    protected fun setupLoggedInUser(userId: Long, blog: Boolean) {
        val accessToken = UUID.randomUUID().toString()
        doReturn(accessToken).whenever(accessTokenStorage).get(any())

        doReturn(
            Session(
                accessToken = accessToken,
                userId = userId,
                accountId = userId * 10,
                loginDateTime = Date(),
            ),
        ).whenever(authBackend).session(accessToken)

        doReturn(
            User(
                id = userId,
                name = "ray.sponsible",
                email = "ray.sponsible@gmail.com",
                pictureUrl = "https://picsum.photos/200/200",
                blog = blog,
            ),
        ).whenever(userBackend).get(userId)
    }

    protected fun driverOptions(): ChromeOptions {
        val options = ChromeOptions()
        options.addArguments("--disable-web-security") // To prevent CORS issues
        options.addArguments("--lang=en")
        options.addArguments("--allowed-ips=")
        options.addArguments("--remote-allow-origins=*")
        options.addArguments("--user-agent =Mozilla/5.0 (iPhone; CPU iPhone OS 10_3 like Mac OS X) AppleWebKit/602.1.50 (KHTML, like Gecko) CriOS/56.0.2924.75 Mobile/14E5239e Safari/602.1")
        if (System.getProperty("headless") == "true") {
            options.addArguments("--headless")
            options.addArguments("--no-sandbox")
            options.addArguments("--disable-dev-shm-usage")
        }
//        options.setBinary("/usr/local/bin/chromium")
//        options.setExperimentalOption(
//            "mobileEmulation",
//            mapOf(
//                "deviceName" to "Nexus 5",
//            ),
//        )

        return options
    }

    @BeforeEach
    fun setUp() {
//        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
//        System.setProperty("webdriver.chrome.whitelistedIps", "")

        this.driver = ChromeDriver(driverOptions())
        this.url = "http://localhost:$port"

        driver.manage().timeouts().implicitlyWait(Duration.of(timeout, ChronoUnit.SECONDS))
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
        "http://localhost:$port/$path"

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

    protected fun click(selector: String) {
        driver.findElement(By.cssSelector(selector)).click()
    }

    protected fun scrollToBottom() {
        val js = driver as JavascriptExecutor
        // Scroll down till the bottom of the page
        // Scroll down till the bottom of the page
        js.executeScript("window.scrollBy(0,document.body.scrollHeight)")
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
