package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.rometools.rome.feed.rss.Channel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.admin.store.StoreProductsControllerTest
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.dto.UnpinStoryCommand
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.blog.product.dto.SearchProductResponse
import com.wutsi.blog.story.dto.RecommendStoryResponse
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.User
import com.wutsi.blog.user.dto.UserSummary
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.Cookie
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BlogControllerTest : SeleniumTestSupport() {
    @Value("\${wutsi.facebook.app-id}")
    protected lateinit var facebookAppId: String

    private val blog = User(
        id = 1,
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

    private val stories = listOf(
        StorySummary(
            id = 100,
            userId = blog.id,
            title = "Story 1",
            thumbnailUrl = "https://picsum.photos/400/400",
            commentCount = 11,
            likeCount = 12,
            shareCount = 13,
            summary = "this is summary 100",
        ),
        StorySummary(
            id = 200,
            userId = blog.id,
            title = "Story 2",
            thumbnailUrl = "https://picsum.photos/450/400",
            commentCount = 20,
            likeCount = 21,
            shareCount = 22,
            summary = "this is summary 200",
        ),
        StorySummary(
            id = 300,
            userId = blog.id,
            title = "Story 3",
            thumbnailUrl = "https://picsum.photos/450/400",
            commentCount = 20,
            likeCount = 21,
            shareCount = 22,
            summary = "this is summary 300",
        ),
        StorySummary(
            id = 400,
            userId = blog.id,
            title = "Story 4",
            thumbnailUrl = "https://picsum.photos/450/400",
            commentCount = 20,
            likeCount = 21,
            shareCount = 22,
            summary = "this is summary 400",
        ),
    )

    private val products = listOf(
        ProductSummary(
            id = 100,
            title = "Product 100",
            imageUrl = "https://picsum.photos/1200/600",
            fileUrl = "https://www.google.ca/123.pdf",
            storeId = StoreProductsControllerTest.STORE_ID,
            userId = StoreProductsControllerTest.BLOG_ID,
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
            storeId = StoreProductsControllerTest.STORE_ID,
            userId = StoreProductsControllerTest.BLOG_ID,
            price = 1000,
            currency = "XAF",
            status = ProductStatus.PUBLISHED,
            available = true,
            slug = "/product/200/product-200",
        ),
        ProductSummary(
            id = 300,
            title = "Product 300",
            imageUrl = "https://picsum.photos/1200/600",
            fileUrl = "https://www.google.ca/123.pdf",
            storeId = StoreProductsControllerTest.STORE_ID,
            userId = StoreProductsControllerTest.BLOG_ID,
            price = 500,
            currency = "XAF",
            status = ProductStatus.PUBLISHED,
            available = true,
            slug = "/product/200/product-200",
        ),
    )

    private val rest = RestTemplate()

    private fun addPresubscribeCookie(blog: User) {
        driver.get(url(url))

        val key = CookieHelper.preSubscribeKey(UserModel(id = blog.id))
        driver.manage().addCookie(Cookie(key, "1"))
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.id)
        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.name)

        doReturn(RecommendStoryResponse(listOf(600))).whenever(storyBackend).recommend(any())

        doReturn(SearchStoryResponse(stories)).whenever(storyBackend).search(any())
        doReturn(
            SearchUserResponse(
                users = listOf(
                    UserSummary(
                        id = blog.id,
                        name = blog.name,
                        pictureUrl = blog.pictureUrl,
                        fullName = blog.fullName,
                    ),
                ),
            ),
        ).whenever(userBackend).search(any())
    }

    @Test
    fun about() {
        driver.get("$url/@/${blog.name}/about")

        assertCurrentPageIs(PageName.BLOG_ABOUT)

        assertElementText("h1", blog.fullName)
        assertElementText("h2", blog.fullName)
        assertElementPresent("a.btn-follow")
    }

    @Test
    fun aboutShareFacebook() {
        driver.get("$url/@/${blog.name}/about")

        click(".share-widget a", 1000)
        assertElementVisible("#share-modal")

        click("#share-modal a[data-target=facebook]", 1000)

        verify(shareBackend, never()).share(any())
    }

    @Test
    fun aboutShareTwitter() {
        driver.get("$url/@/${blog.name}/about")

        click(".share-widget a", 1000)
        assertElementVisible("#share-modal")

        click("#share-modal a[data-target=twitter]", 1000)

        verify(shareBackend, never()).share(any())
    }

    @Test
    fun aboutNotFound() {
        val ex = HttpClientErrorException.create(
            HttpStatus.NOT_FOUND,
            "Not found",
            HttpHeaders(),
            "".toByteArray(),
            Charsets.UTF_8,
        )
        doThrow(ex).whenever(userBackend).get(any<String>())

        driver.get("$url/@/${blog.name}/about")

        assertCurrentPageIs(PageName.BLOG_NOT_FOUND)
    }

    @Test
    fun blog() {
        // GIVEN
        addPresubscribeCookie(blog)

        // WHEN
        driver.get("$url/@/${blog.name}")

        assertCurrentPageIs(PageName.BLOG)

        // Header
        assertElementAttribute("html", "lang", "en")
        assertElementAttribute("head title", "text", "${blog.fullName} | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", blog.biography)
        assertElementAttribute("head meta[name='robots']", "content", "index,follow")

        assertElementAttribute("head meta[property='og:title']", "content", blog.fullName)
        assertElementAttribute("head meta[property='og:description']", "content", blog.biography)
        assertElementAttribute("head meta[property='og:type']", "content", "profile")
        assertElementAttributeEndsWith("head meta[property='og:url']", "content", "/@/${blog.name}")
        assertElementAttribute(
            "head meta[property='og:image']",
            "content",
            "http://localhost:0/@/${blog.name}/image.png",
        )
        assertElementAttribute("head meta[property='og:site_name']", "content", "Wutsi")

        assertElementAttribute("head meta[name='twitter:card']", "content", "summary_large_image")
        assertElementAttribute("head meta[name='twitter:title']", "content", blog.fullName)
        assertElementAttribute("head meta[name='twitter:description']", "content", blog.biography)
        assertElementAttribute(
            "head meta[name='twitter:image']",
            "content",
            "http://localhost:0/@/${blog.name}/image.png",
        )

        assertElementAttribute("head meta[name='facebook:app_id']", "content", facebookAppId)

        assertElementAttributeEndsWith("head link[type='application/rss+xml']", "href", "/@/${blog.name}/rss")

        assertElementAttributeEndsWith("head link[type='application/rss+xml']", "href", "/@/${blog.name}/rss")

        assertElementAttributeEndsWith("head link[rel='shortcut icon']", "href", "/assets/wutsi/img/favicon.ico")

        // Content
        assertElementText("h1", blog.fullName)
        assertElementPresent("#story-card-100")
        assertElementPresent("#story-card-200")
        assertElementNotPresent(".story-card-pinned")

        // KPI not visible
        assertElementNotPresent("#kpi-overview")

        // Load More
        assertElementNotPresent("#story-load-more")

        // Store
        assertElementNotPresent("#shop-panel")
    }

    @Test
    fun blogWithStore() {
        // GIVEN
        setupLoggedInUser(userId = blog.id, walletId = "wallet-id", storeId = "store-id")
        doReturn(SearchProductResponse(products)).whenever(productBackend).search(any())

        addPresubscribeCookie(blog)

        // WHEN
        driver.get("$url/@/${blog.name}")

        assertCurrentPageIs(PageName.BLOG)

        assertElementPresent("#shop-panel")
        assertElementCount("#shop-panel .product-summary-attribute", 3)
    }

    @Test
    fun loadMore() {
        // GIVEN
        addPresubscribeCookie(blog)

        val xstories = (0..BlogController.LIMIT).map {
            StorySummary(
                id = it + 1L,
                userId = blog.id,
                title = "Story 3",
                thumbnailUrl = "https://picsum.photos/450/400",
                commentCount = 20,
                likeCount = 21,
                shareCount = 22,
                summary = "this is summary 300",
            )
        }
        doReturn(SearchStoryResponse(xstories)).whenever(storyBackend).search(any())

        // WHEN
        driver.get("$url/@/${blog.name}")

        scrollToBottom()
        assertElementPresent("#story-load-more")

        doReturn(SearchStoryResponse(stories)).whenever(storyBackend).search(any())
        click("#story-load-more", 1000)
        assertElementNotPresent("#story-load-more")
    }

    @Test
    fun myBlog() {
        // GIVEN
        setupLoggedInUser(
            userId = blog.id,
            userName = blog.name,
            blog = true,
            walletId = "1111",
            fullName = blog.fullName,
            email = blog.email,
            pictureUrl = blog.pictureUrl,
        )

        // WHEN
        driver.get("$url/@/${blog.name}")

        assertCurrentPageIs(PageName.BLOG)

        // Header
        assertElementAttribute("html", "lang", "en")
        assertElementAttribute("head title", "text", "${blog.fullName} | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", blog.biography)
        assertElementAttribute("head meta[name='robots']", "content", "index,follow")

        assertElementAttribute("head meta[property='og:title']", "content", blog.fullName)
        assertElementAttribute("head meta[property='og:description']", "content", blog.biography)
        assertElementAttribute("head meta[property='og:type']", "content", "profile")
        assertElementAttributeEndsWith("head meta[property='og:url']", "content", "/@/${blog.name}")
        assertElementAttribute(
            "head meta[property='og:image']",
            "content",
            "http://localhost:0/@/${blog.name}/image.png",
        )
        assertElementAttribute("head meta[property='og:site_name']", "content", "Wutsi")

        assertElementAttribute("head meta[name='twitter:card']", "content", "summary_large_image")
        assertElementAttribute("head meta[name='twitter:title']", "content", blog.fullName)
        assertElementAttribute("head meta[name='twitter:description']", "content", blog.biography)
        assertElementAttribute(
            "head meta[name='twitter:image']",
            "content",
            "http://localhost:0/@/${blog.name}/image.png",
        )

        assertElementAttribute("head meta[name='facebook:app_id']", "content", facebookAppId)

        assertElementAttributeEndsWith("head link[type='application/rss+xml']", "href", "/@/${blog.name}/rss")

        assertElementAttributeEndsWith("head link[type='application/rss+xml']", "href", "/@/${blog.name}/rss")

        assertElementAttributeEndsWith("head link[rel='shortcut icon']", "href", "/assets/wutsi/img/favicon.ico")

        // Content
        assertElementText("h1", blog.fullName)
        assertElementPresent("#story-card-100")
        assertElementPresent("#story-card-200")

        // KPI not visible
        assertElementPresent("#kpi-overview")
        assertElementPresent("#kpi-overview-read")
        assertElementPresent("#kpi-overview-subscriber")
        assertElementPresent("#kpi-overview-balance")
        assertElementPresent("#kpi-overview-more")
    }

    @Test
    fun blogWithPinnedStory() {
        // GIVEN
        addPresubscribeCookie(blog)

        val xblog = blog.copy(pinStoryId = stories[1].id)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.id)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.name)

        // WHEN
        driver.get("$url/@/${blog.name}")

        assertCurrentPageIs(PageName.BLOG)

        assertElementPresent("#story-card-100")
        assertElementPresent("#story-card-200")
        assertElementPresent(".story-card-pinned")
    }

    @Test
    fun pinStory() {
        // GIVEN
        setupLoggedInUser(
            userId = blog.id,
            userName = blog.name,
            blog = true,
            walletId = "1111",
            fullName = blog.fullName,
            email = blog.email,
            pictureUrl = blog.pictureUrl,
        )

        // WHEN
        driver.get("$url/@/${blog.name}")

        click("#story-card-200 .pin-widget a", 1000)
        assertElementPresent(".story-card-pinned")

        val cmd = argumentCaptor<PinStoryCommand>()
        verify(pinBackend).pin(cmd.capture())
        assertEquals(200L, cmd.firstValue.storyId)
    }

    @Test
    fun unpinStory() {
        // GIVEN
        setupLoggedInUser(userId = blog.id, blog = true)

        val xblog = blog.copy(pinStoryId = stories[1].id)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.id)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.name)

        // WHEN
        driver.get("$url/@/${blog.name}")

        click("#story-card-${stories[1].id} .pin-widget a", 1000)
        assertElementNotPresent(".story-card-pinned")

        val cmd = argumentCaptor<UnpinStoryCommand>()
        verify(pinBackend).unpin(cmd.capture())
        assertEquals(stories[1].id, cmd.firstValue.storyId)
    }

    @Test
    fun notFound() {
        // GIVEN
        val ex = HttpClientErrorException.create(
            HttpStatus.NOT_FOUND,
            "Not found",
            HttpHeaders(),
            "".toByteArray(),
            Charsets.UTF_8,
        )
        doThrow(ex).whenever(userBackend).get(any<String>())

        doReturn(
            SearchUserResponse(
                users = listOf(
                    UserSummary(
                        id = 1,
                        name = "yo",
                        pictureUrl = blog.pictureUrl,
                        fullName = "Yo",
                    ),
                    UserSummary(
                        id = 2,
                        name = "man",
                        pictureUrl = blog.pictureUrl,
                        fullName = "Man",
                    ),
                ),
            ),
        ).whenever(userBackend).search(any())

        // WHEN
        driver.get("$url/@/xxxx")

        assertCurrentPageIs(PageName.BLOG_NOT_FOUND)
    }

    @Test
    fun rss() {
        // WHEN
        val response = rest.getForEntity("$url/@/${blog.name}/rss", Channel::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val channel = response.body!!
        assertEquals("${blog.fullName}(@${blog.name}) RSS Feed", channel.title)
        assertEquals(blog.biography, channel.description)
        assertTrue(channel.link.endsWith("/@/${blog.name}"))

        assertEquals(4, channel.items.size)

        assertEquals(stories[0].title, channel.items[0].title)
        assertEquals(stories[0].summary, channel.items[0].description.value)
        assertEquals(blog.fullName, channel.items[0].author)
        assertTrue(channel.items[0].link.endsWith(stories[0].slug))
        assertEquals(stories[0].thumbnailUrl, channel.items[0].enclosures[0].url)

        assertEquals(stories[1].title, channel.items[1].title)
        assertEquals(stories[1].summary, channel.items[1].description.value)
        assertEquals(blog.fullName, channel.items[1].author)
        assertTrue(channel.items[1].link.endsWith(stories[1].slug))
        assertEquals(stories[1].thumbnailUrl, channel.items[1].enclosures[0].url)

        assertEquals(stories[2].title, channel.items[2].title)
        assertEquals(stories[2].summary, channel.items[2].description.value)
        assertEquals(blog.fullName, channel.items[2].author)
        assertTrue(channel.items[2].link.endsWith(stories[2].slug))
        assertEquals(stories[2].thumbnailUrl, channel.items[2].enclosures[0].url)

        assertEquals(stories[3].title, channel.items[3].title)
        assertEquals(stories[3].summary, channel.items[3].description.value)
        assertEquals(blog.fullName, channel.items[3].author)
        assertTrue(channel.items[3].link.endsWith(stories[3].slug))
        assertEquals(stories[3].thumbnailUrl, channel.items[3].enclosures[0].url)
    }

    @Test
    fun subscribe() {
        // GIVEN
        val subscriber = setupLoggedInUser(100, language = "en")

        val users = listOf(
            UserSummary(
                id = 10,
                name = "ray.sponsible",
                blog = true,
                subscriberCount = 100,
                pictureUrl = "https://picsum.photos/200/200",
                biography = "Biography of the user ...",
                language = "en",
            ),
            UserSummary(
                id = 20,
                name = "roger.milla",
                blog = true,
                subscriberCount = 10,
                pictureUrl = "https://picsum.photos/100/100",
                biography = "Biography of the user ...",
                language = "en",
            ),
            UserSummary(
                id = 30,
                name = "samuel.etoo",
                blog = true,
                subscriberCount = 30,
                pictureUrl = "https://picsum.photos/128/128",
                biography = "Biography of the user ...",
                language = "en",
            ),
        )
        doReturn(SearchUserResponse(users)).whenever(userBackend).search(any())

        // WHEN
        driver.get("$url/@/${blog.name}")

        assertCurrentPageIs(PageName.BLOG)

        scroll(.3)
        click(".btn-follow")

        val command = argumentCaptor<SubscribeCommand>()
        verify(subscriptionBackend).subscribe(command.capture())
        assertEquals(blog.id, command.firstValue.userId)
        assertEquals(subscriber.id, command.firstValue.subscriberId)
        assertNull(command.firstValue.storyId)
        assertEquals("blog", command.firstValue.referer)

        assertCurrentPageIs(PageName.SUBSCRIBE)

        click("#btn-continue")
        assertCurrentPageIs(PageName.BLOG)
    }

    @Test
    fun `pre-subscribe for anonymous users`() {
        // WHEN
        driver.get("$url/@/${blog.name}")

        assertCurrentPageIs(PageName.BLOG)

        // Header
        assertElementAttribute("html", "lang", "en")
        assertElementAttribute("head title", "text", "${blog.fullName} | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", blog.biography)
        assertElementAttribute("head meta[name='robots']", "content", "index,follow")

        assertElementAttribute("head meta[property='og:title']", "content", blog.fullName)
        assertElementAttribute("head meta[property='og:description']", "content", blog.biography)
        assertElementAttribute("head meta[property='og:type']", "content", "profile")
        assertElementAttributeEndsWith("head meta[property='og:url']", "content", "/@/${blog.name}")
        assertElementAttribute(
            "head meta[property='og:image']",
            "content",
            "http://localhost:0/@/${blog.name}/image.png",
        )
        assertElementAttribute("head meta[property='og:site_name']", "content", "Wutsi")

        assertElementAttribute("head meta[name='twitter:card']", "content", "summary_large_image")
        assertElementAttribute("head meta[name='twitter:title']", "content", blog.fullName)
        assertElementAttribute("head meta[name='twitter:description']", "content", blog.biography)
        assertElementAttribute(
            "head meta[name='twitter:image']",
            "content",
            "http://localhost:0/@/${blog.name}/image.png",
        )

        assertElementAttribute("head meta[name='facebook:app_id']", "content", facebookAppId)

        assertElementAttributeEndsWith("head link[type='application/rss+xml']", "href", "/@/${blog.name}/rss")

        assertElementAttributeEndsWith("head link[type='application/rss+xml']", "href", "/@/${blog.name}/rss")

        assertElementAttributeEndsWith("head link[rel='shortcut icon']", "href", "/assets/wutsi/img/favicon.ico")

        // Content
        assertElementPresent("#pre-subscribe-container")

        val key = CookieHelper.preSubscribeKey(UserModel(id = blog.id))
        val cookie = driver.manage().getCookieNamed(key)
        assertNotNull(cookie)
        assertEquals("1", cookie.value)
    }

    @Test
    fun `pre-subscribe for non-suscriber`() {
        // GIVEN
        setupLoggedInUser(333)

        // WHEN
        driver.get("$url/@/${blog.name}")

        // Content
        assertElementPresent("#pre-subscribe-container")

        val key = CookieHelper.preSubscribeKey(UserModel(id = blog.id))
        val cookie = driver.manage().getCookieNamed(key)
        assertNotNull(cookie)
        assertEquals("1", cookie.value)
    }

    @Test
    fun `should never pre-subscribe for suscriber`() {
        // GIVEN
        setupLoggedInUser(333)

        val xblog = blog.copy(subscribed = true)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.id)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.name)

        // WHEN
        driver.get("$url/@/${xblog.name}")

        // Content
        assertElementNotPresent("#pre-subscribe-container")
        assertElementPresent("#story-card-100")
        assertElementPresent("#story-card-200")
    }

    @Test
    fun `should never pre-subscribe for empty blog`() {
        // GIVEN
        setupLoggedInUser(333)

        val xblog = blog.copy(publishStoryCount = 0)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.id)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.name)

        // WHEN
        driver.get("$url/@/${xblog.name}")

        // Content
        assertElementNotPresent("#pre-subscribe-container")
        assertElementPresent("#story-card-100")
        assertElementPresent("#story-card-200")
    }

    @Test
    fun image() {
        val img = ImageIO.read(URL("http://localhost:$port/@/${blog.name}/image.png"))

        assertEquals(1200, img.width)
        assertEquals(630, img.height)
    }

    @Test
    fun `no image for user`() {
        val xblog = blog.copy(blog = false)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.name)

        val cnn = URL("http://localhost:$port/@/${blog.name}/image.png").openConnection() as HttpURLConnection
        try {
            assertEquals(404, cnn.responseCode)
        } finally {
            cnn.disconnect()
        }
    }
}
