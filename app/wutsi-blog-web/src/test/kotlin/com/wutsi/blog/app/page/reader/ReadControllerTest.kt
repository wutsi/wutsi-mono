package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.comment.dto.Comment
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.comment.dto.SearchCommentResponse
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.UnlikeStoryCommand
import com.wutsi.blog.share.dto.ShareStoryCommand
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.SearchSimilarStoryResponse
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.SearchTopicResponse
import com.wutsi.blog.story.dto.Story
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.story.dto.Tag
import com.wutsi.blog.story.dto.Topic
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.User
import com.wutsi.blog.user.dto.UserSummary
import com.wutsi.tracking.manager.dto.PushTrackRequest
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ReadControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 1L
        const val USER_ID = 100L
        const val STORY_ID = 888L
    }

    private val topics = listOf(
        Topic(id = 100, name = "Topic 100"),
        Topic(id = 101, name = "Topic 101"),
        Topic(id = 102, name = "Topic 102"),
    )
    private val story = Story(
        id = STORY_ID,
        userId = BLOG_ID,
        title = "Ukraine: Finalement la paix! Poutine et Zelynski font un calin",
        tagline = "Il etait temps!!!",
        content = IOUtils.toString(ReadControllerTest::class.java.getResourceAsStream("/story.json")),
        slug = "/read/$STORY_ID/ukraine-finalement-la-paix",
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
    private val users = listOf(
        UserSummary(
            id = BLOG_ID,
            fullName = "Test Blog",
            pictureUrl = "https://picsum.photos/100/100",
        ),
        UserSummary(
            id = USER_ID,
            fullName = "Ray Sponsible",
            pictureUrl = "https://picsum.photos/100/100",
        ),
    )
    private val comments = listOf(
        Comment(
            storyId = STORY_ID,
            userId = USER_ID,
            text = "Yo man",
        ),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetUserResponse(blog)).whenever(userBackend).get(BLOG_ID)

        doReturn(SearchTopicResponse(topics)).whenever(topicBackend).all()

        doReturn(GetStoryResponse(story)).whenever(storyBackend).get(any())
        doReturn(SearchSimilarStoryResponse(seeAlso.map { it.id })).whenever(storyBackend).searchSimilar(any())
        doReturn(SearchStoryResponse(seeAlso)).whenever(storyBackend).search(any())

        doReturn(SearchUserResponse(users)).whenever(userBackend).search(any())

        doReturn(SearchCommentResponse(comments)).whenever(commentBackend).search(any())
    }

    @Test
    fun anonymous() {
        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        // Header
        assertElementAttribute("html", "lang", story.language)
        assertElementAttribute("head title", "text", "${story.title} | ${blog.fullName} | Wutsi")
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

        // Social action
        assertElementPresent("#like-widget-$STORY_ID")
        assertElementPresent("#comment-widget-$STORY_ID")
        assertElementPresent("#share-widget-$STORY_ID")

        // Tracking
        val track = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(track.capture())
        assertEquals(STORY_ID.toString(), track.firstValue.productId.toString())
        assertEquals(PageName.READ, track.firstValue.page)
        assertEquals(
            driver.findElement(By.cssSelector("head meta[name='wutsi:hit_id")).getAttribute("content"),
            track.firstValue.correlationId,
        )
        assertNull(track.firstValue.accountId)
    }

    @Test
    fun loggedIn() {
        // GIVEN
        setupLoggedInUser(100, blog = false, walletId = null)

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        // Header
        assertElementAttribute("html", "lang", story.language)
        assertElementAttribute("head title", "text", "${story.title} | ${blog.fullName} | Wutsi")
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

        // Social action
        assertElementPresent("#like-widget-$STORY_ID")
        assertElementPresent("#comment-widget-$STORY_ID")
        assertElementPresent("#share-widget-$STORY_ID")

        // Tracking
        val track = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(track.capture())
        assertEquals(STORY_ID.toString(), track.firstValue.productId.toString())
        assertEquals(PageName.READ, track.firstValue.page)
        assertEquals(
            driver.findElement(By.cssSelector("head meta[name='wutsi:hit_id")).getAttribute("content"),
            track.firstValue.correlationId,
        )
        assertEquals("100", track.firstValue.accountId)
    }

    @Test
    fun `request subscription from anonymous reader from Blog page`() {
        // WHEN
        navigate("$url/read/$STORY_ID?utm_from=blog")
        assertCurrentPageIs(PageName.READ)

        // THEN
        assertElementPresent("#pre-subscribe-container")
        assertElementAttributeEndsWith(
            ".btn-follow",
            "href",
            "/@/${blog.name}/subscribe?story-id=$STORY_ID&return-url=${story.slug}?utm_from=pre_subscribe",
        )
        assertElementAttributeEndsWith("#btn-no-thanks", "href", "${story.slug}?utm_from=pre_subscribe")

        verify(trackingBackend, never()).push(any())
    }

    @Test
    fun `request subscription from un-subscribed reader from Inbox page`() {
        // GIVEN
        setupLoggedInUser(100, blog = false, walletId = null)

        // WHEN
        navigate("$url/read/$STORY_ID?utm_from=inbox")
        assertCurrentPageIs(PageName.READ)

        // THEN
        assertElementPresent("#pre-subscribe-container")
        assertElementAttributeEndsWith(
            ".btn-follow",
            "href",
            "/@/${blog.name}/subscribe?story-id=$STORY_ID&return-url=${story.slug}?utm_from=pre_subscribe",
        )
        assertElementAttributeEndsWith("#btn-no-thanks", "href", "${story.slug}?utm_from=pre_subscribe")

        verify(trackingBackend, never()).push(any())
    }

    @Test
    fun notPublished() {
        // GIVEN
        doReturn(GetStoryResponse(story.copy(status = StoryStatus.DRAFT))).whenever(storyBackend).get(any())

        // WHEN
        navigate("$url/read/333")
        assertCurrentPageIs(PageName.STORY_NOT_FOUND)
    }

    @Test
    fun notFound() {
        // GIVEN
        val ex = HttpClientErrorException(HttpStatus.NOT_FOUND)
        doThrow(ex).whenever(storyBackend).get(any())

        // WHEN
        navigate("$url/read/99999")
        assertCurrentPageIs(PageName.STORY_NOT_FOUND)
    }

    @Test
    fun likeOnLoad() {
        // GIVEN
        navigate("$url/read/$STORY_ID?like=1&like-key=540540950_$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        val like = argumentCaptor<LikeStoryCommand>()
        verify(likeBackend).like(like.capture())
        assertEquals(STORY_ID, like.firstValue.storyId)
    }

    @Test
    fun likeAStory() {
        // WHEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#like-widget-$STORY_ID a", 1000)

        // THEN
        val command = argumentCaptor<LikeStoryCommand>()
        verify(likeBackend).like(command.capture())
        assertEquals(STORY_ID, command.firstValue.storyId)
        assertNull(command.firstValue.userId)
        assertNotNull(command.firstValue.deviceId)
    }

    @Test
    fun unlikeAsStory() {
        // GIVEN
        setupLoggedInUser(USER_ID)
        doReturn(GetStoryResponse(story.copy(liked = true))).whenever(storyBackend).get(any())

        // THEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#like-widget-$STORY_ID a")

        // THEN
        val command = argumentCaptor<UnlikeStoryCommand>()
        verify(likeBackend).unlike(command.capture())
        assertEquals(STORY_ID, command.firstValue.storyId)
        assertEquals(USER_ID, command.firstValue.userId)
        assertNotNull(command.firstValue.deviceId)
    }

    @Test
    fun shareOnLoad() {
        // GIVEN
        navigate("$url/read/$STORY_ID?share=1")
        assertCurrentPageIs(PageName.READ)

        // THEN
//        Thread.sleep(5000) // The delay is very flaky
//        assertElementVisible("#share-modal")
    }

    @Test
    fun shareToFacebook() {
        // THEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#share-widget-$STORY_ID a")

        // THEN
        assertElementVisible("#share-modal")
        Thread.sleep(1000)
        click("#share-modal a[data-target=facebook]")
        Thread.sleep(1000)

        val command = argumentCaptor<ShareStoryCommand>()
        verify(shareBackend).share(command.capture())
        assertEquals(STORY_ID, command.firstValue.storyId)
        assertNull(command.firstValue.userId)
    }

    @Test
    fun shareToTwitter() {
        // GIVEN
        setupLoggedInUser(USER_ID)
        doReturn(GetStoryResponse(story.copy(liked = true))).whenever(storyBackend).get(any())

        // THEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#share-widget-$STORY_ID a")

        // THEN
        assertElementVisible("#share-modal")
        Thread.sleep(1000)
        click("#share-modal a[data-target=twitter]")
        Thread.sleep(1000)

        val command = argumentCaptor<ShareStoryCommand>()
        verify(shareBackend).share(command.capture())
        assertEquals(STORY_ID, command.firstValue.storyId)
        assertEquals(USER_ID, command.firstValue.userId)
        assertNotNull(command.firstValue.userId)
    }

    @Test
    fun shareToLinkedin() {
        // THEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#share-widget-$STORY_ID a")

        // THEN
        assertElementVisible("#share-modal")
        Thread.sleep(1000)
        click("#share-modal a[data-target=linkedin]")
        Thread.sleep(1000)

        val command = argumentCaptor<ShareStoryCommand>()
        verify(shareBackend).share(command.capture())
        assertEquals(STORY_ID, command.firstValue.storyId)
        assertNull(command.firstValue.userId)
    }

    @Test
    fun anonymousCannotComment() {
        // THEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#comment-widget-$STORY_ID a")

        // THEN
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun userCanComment() {
        // GIVEN
        setupLoggedInUser(USER_ID)
        doReturn(GetStoryResponse(story)).whenever(storyBackend).get(any())

        // THEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#comment-widget-$STORY_ID a")

        // THEN
        assertCurrentPageIs(PageName.COMMENT)

        input("#comment-text", "This is a comment")
        click("#btn-submit-comment")
        Thread.sleep(1000)

        val command = argumentCaptor<CommentStoryCommand>()
        verify(commentBackend).comment(command.capture())
        assertEquals(STORY_ID, command.firstValue.storyId)
        assertEquals(USER_ID, command.firstValue.userId)
        assertEquals("This is a comment", command.firstValue.text)
    }
}
