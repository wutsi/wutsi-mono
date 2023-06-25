package com.wutsi.blog.app.page.reader

import com.amazonaws.util.IOUtils
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.Story
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.story.dto.Tag
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.User
import com.wutsi.blog.user.dto.UserSummary
import com.wutsi.tracking.manager.dto.PushTrackRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReadControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 1L
        const val USER_ID = 100L
        const val STORY_ID = 888L
    }

    private val story = Story(
        id = STORY_ID,
        userId = BLOG_ID,
        title = "Ukraine: Finalement la paix! Poutine et Zelynski font un calin",
        tagline = "Il etait temps!!!",
        content = IOUtils.toString(ReadControllerTest::class.java.getResourceAsStream("/story.json")),
        slug = "/story/$STORY_ID/ukraine-finalement-la-paix",
        thumbnailUrl = "https://picsum.photos/1200/800",
        language = "en",
        summary = "This is the summary of the story",
        tags = listOf("Ukraine", "Russie", "Poutine", "Zelynsky", "Guerre").map { Tag(name = it) },
        creationDateTime = Date(),
        modificationDateTime = Date(),
        status = StoryStatus.PUBLISHED,
    )
    private val seeAlso = listOf(
        StorySummary(
            id = 111L,
            userId = BLOG_ID,
            title = "This is the first story recommended",
            thumbnailUrl = "https://picsum.photos/1200/800",
            status = StoryStatus.PUBLISHED,
            liked = true,
            likeCount = 30L,
            commentCount = 150L,
            shareCount = 11,
            shared = false,
        ),
        StorySummary(
            id = 112L,
            userId = BLOG_ID,
            title = "This is the second story recommended",
            thumbnailUrl = null,
            status = StoryStatus.PUBLISHED,
            liked = false,
            likeCount = 1L,
            commentCount = 5L,
            commented = true,
            shareCount = 150,
            shared = true,
        ),
    )
    private val blog = User(
        id = BLOG_ID,
        blog = true,
        name = "test",
        fullName = "Test Blog",
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetUserResponse(blog)).whenever(userBackend).get(BLOG_ID)

        doReturn(GetStoryResponse(story)).whenever(storyBackend).get(any())
        doReturn(SearchStoryResponse(seeAlso)).whenever(storyBackend).search(any())

        doReturn(SearchUserResponse(listOf(UserSummary(id = BLOG_ID)))).whenever(userBackend).search(any())
    }

    @Test
    fun anonymous() {
        // WHEN
        driver.get("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        // Header
        assertElementAttribute("html", "lang", story.language)
        assertElementAttribute("head title", "text", "${story.title} | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", story.summary)
        assertElementAttribute("head meta[name='robots']", "content", "index,follow")

        // OpenGraph
        assertElementAttribute("head meta[property='og:title']", "content", story.title)
        assertElementAttribute("head meta[property='og:description']", "content", story.summary)
        assertElementAttribute("head meta[property='og:type']", "content", "article")
        assertElementAttributeEndsWith("head meta[property='og:url']", "content", story.slug)
        assertElementAttribute("head meta[property='og:image']", "content", story.thumbnailUrl)
        assertElementAttribute("head meta[property='og:site_name']", "content", "Wutsi")
        assertElementAttribute("head meta[property='article:author']", "content", blog.fullName)
        assertElementCount("head meta[property='article:tag']", story.tags.size)

        // Wutsi
        assertElementAttribute("head meta[name='wutsi:story_id']", "content", story.id.toString())
        assertElementPresent("head meta[name='wutsi:hit_id']")

        // Facebook
        assertElementAttribute("head meta[name='facebook:app_id']", "content", "629340480740249")
        assertElementPresent("script#fb-pixel-code")

        // Google Analytics
        assertElementPresent("script#ga-code")

        // Recommendations
        assertElementCount("#recommendation-container .story-summary-card", seeAlso.size)

        // Tracking
        val track = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(track.capture())
        assertEquals(STORY_ID.toString(), track.firstValue.productId.toString())
        assertEquals(true, track.firstValue.url?.endsWith(story.slug))
        assertEquals(PageName.READ, track.firstValue.page)
        assertEquals(
            driver.findElement(By.cssSelector("head meta[name='wutsi:hit_id")).getAttribute("content"),
            track.firstValue.correlationId,
        )
        assertNull(track.firstValue.accountId)
    }

    @Test
    fun notPublished() {
        // GIVEN
        doReturn(story.copy(status = StoryStatus.DRAFT)).whenever(storyBackend).get(any())

        // WHEN
        driver.get("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.STORY_NOT_FOUND)
    }

    @Test
    fun notFound() {
        // GIVEN
        val ex = HttpClientErrorException(HttpStatus.NOT_FOUND, "")
        doThrow(ex).whenever(storyBackend).get(any())

        // WHEN
        driver.get("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.STORY_NOT_FOUND)
    }

    @Test
    fun likeOnLoad() {
        // GIVEN
        driver.get("$url/read/$STORY_ID?like=1&like-key=540540950_$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // Like
        val like = argumentCaptor<LikeStoryCommand>()
        verify(likeBackend).like(like.capture())
        assertEquals(STORY_ID, like.firstValue.storyId)
    }
}
