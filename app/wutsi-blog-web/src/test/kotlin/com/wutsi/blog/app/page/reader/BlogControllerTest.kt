package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.rometools.rome.feed.rss.Channel
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
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
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
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

    private val rest = RestTemplate()

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
        assertElementText("h2", "About ${blog.fullName}")
        assertElementPresent("a.btn-follow")
    }

    @Test
    fun blog() {
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
        assertElementAttribute("head meta[property='og:image']", "content", blog.pictureUrl)
        assertElementAttribute("head meta[property='og:site_name']", "content", "Wutsi")

        assertElementAttribute("head meta[name='twitter:card']", "content", "summary_large_image")
        assertElementAttribute("head meta[name='twitter:title']", "content", blog.fullName)
        assertElementAttribute("head meta[name='twitter:description']", "content", blog.biography)
        assertElementAttribute("head meta[name='twitter:image']", "content", blog.pictureUrl)

        assertElementAttribute("head meta[name='facebook:app_id']", "content", facebookAppId)

        assertElementAttributeEndsWith("head link[type='application/rss+xml']", "href", "/@/${blog.name}/rss")

        assertElementAttributeEndsWith("head link[type='application/rss+xml']", "href", "/@/${blog.name}/rss")

        assertElementAttributeEndsWith("head link[rel='shortcut icon']", "href", "/assets/wutsi/img/favicon.ico")

        // Content
        assertElementText("h1", blog.fullName)
        assertElementPresent("#story-card-100")
        assertElementPresent("#story-card-200")

        // KPI not visible
        assertElementNotPresent("#kpi-overview")
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
        assertElementAttribute("head meta[property='og:image']", "content", blog.pictureUrl)
        assertElementAttribute("head meta[property='og:site_name']", "content", "Wutsi")

        assertElementAttribute("head meta[name='twitter:card']", "content", "summary_large_image")
        assertElementAttribute("head meta[name='twitter:title']", "content", blog.fullName)
        assertElementAttribute("head meta[name='twitter:description']", "content", blog.biography)
        assertElementAttribute("head meta[name='twitter:image']", "content", blog.pictureUrl)

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
        val subscriber = setupLoggedInUser(100)

        // WHEN
        driver.get("$url/@/${blog.name}")

        assertCurrentPageIs(PageName.BLOG)

        scroll(.3)
        click(".btn-follow")

        val command = argumentCaptor<SubscribeCommand>()
        verify(subscriptionBackend).subscribe(command.capture())
        assertEquals(blog.id, command.firstValue.userId)
        assertEquals(subscriber.id, command.firstValue.subscriberId)
    }
}
