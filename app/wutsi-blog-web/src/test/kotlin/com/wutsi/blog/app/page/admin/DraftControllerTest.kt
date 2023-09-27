package com.wutsi.blog.app.page.admin

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.DeleteStoryCommand
import com.wutsi.blog.story.dto.GetStoryReadabilityResponse
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.Story
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.story.dto.Tag
import com.wutsi.blog.story.dto.Topic
import com.wutsi.blog.user.dto.Readability
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UserSummary
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date
import kotlin.test.assertEquals

class DraftControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 1L
    }

    private val story = Story(
        id = 111L,
        userId = BLOG_ID,
        title = "Ukraine: Finalement la paix! Poutine et Zelynski font un calin",
        tagline = "Il etait temps!!!",
        content = IOUtils.toString(PreviewControllerTest::class.java.getResourceAsStream("/story.json")),
        slug = "/read/${PreviewControllerTest.STORY_ID}/ukraine-finalement-la-paix",
        thumbnailUrl = "https://picsum.photos/1200/800",
        language = "en",
        summary = "This is the summary of the story",
        tags = listOf("Ukraine", "Russie", "Poutine", "Zelynsky", "Guerre").map { Tag(name = it) },
        creationDateTime = Date(),
        modificationDateTime = Date(),
        status = StoryStatus.DRAFT,
        topic = Topic(
            id = 100,
            name = "Topic 100",
        ),
        likeCount = 10,
        liked = false,
        commentCount = 300,
        shareCount = 3500,
    )
    private val stories = listOf(
        StorySummary(
            id = 111L,
            userId = BLOG_ID,
            title = "This is the first story recommended",
            thumbnailUrl = "https://picsum.photos/1200/800",
            status = StoryStatus.DRAFT,
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
            status = StoryStatus.DRAFT,
            liked = false,
            likeCount = 1L,
            commentCount = 5L,
            commented = true,
            shareCount = 150,
            shared = true,
        ),
    )
    private val users = listOf(
        UserSummary(
            id = BLOG_ID,
            fullName = "Test Blog",
            pictureUrl = "https://picsum.photos/100/100",
        ),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        setupLoggedInUser(BLOG_ID, blog = true)
        doReturn(SearchStoryResponse(stories)).whenever(storyBackend).search(any())
        doReturn(SearchUserResponse(users)).whenever(userBackend).search(any())
        doReturn(GetStoryResponse(story)).whenever(storyBackend).get(any())
        doReturn(GetStoryReadabilityResponse(Readability())).whenever(storyBackend).readability(any())
    }

    @Test
    fun draft() {
        // WHEN
        navigate("$url/me/draft")

        // THEN
        assertCurrentPageIs(PageName.STORY_DRAFT)
    }

    @Test
    fun delete() {
        // WHEN
        navigate("$url/me/draft")

        // THEN
        click("#story-${stories[0].id} .dropdown-toggle")
        click(".menu-item-delete", 1000)

        driver.switchTo().alert().accept();
        Thread.sleep(1000)

        val cmd = argumentCaptor<DeleteStoryCommand>()
        verify(storyBackend).delete(cmd.capture())
        assertEquals(stories[0].id, cmd.firstValue.storyId)
    }

    @Test
    fun publish() {
        // WHEN
        navigate("$url/me/draft")

        // THEN
        click("#story-${stories[0].id} .dropdown-toggle")
        click(".menu-item-publish")

        assertCurrentPageIs(PageName.EDITOR_READABILITY)
    }

    @Test
    fun preview() {
        // WHEN
        navigate("$url/me/draft")

        // THEN
        click("#story-${stories[0].id} .dropdown-toggle")
        click(".menu-item-preview", 1000)

//        assertCurrentPageIs(PageName.STORY_PREVIEW)
    }
}
