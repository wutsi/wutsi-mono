package com.wutsi.blog.app.page.admin

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.SearchTopicResponse
import com.wutsi.blog.story.dto.Story
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.Tag
import com.wutsi.blog.story.dto.Topic
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.User
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class PreviewControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 1L
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
        content = IOUtils.toString(PreviewControllerTest::class.java.getResourceAsStream("/story.json")),
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
        doReturn(SearchTopicResponse(topics)).whenever(topicBackend).all()
        doReturn(GetStoryResponse(story)).whenever(storyBackend).get(any())
    }

    @Test
    fun preview() {
        // GIVEN
        setupLoggedInUser(BLOG_ID, blog = true)

        // WHEN
        navigate("$url/me/story/$STORY_ID/preview")

        assertCurrentPageIs(PageName.STORY_PREVIEW)

        // THEN
        assertElementNotPresent("#like-widget-$STORY_ID") // No widgets
        assertElementNotPresent("#comment-widget-$STORY_ID")
        assertElementNotPresent("#share-widget-$STORY_ID")

        verify(trackingBackend, never()).push(any()) // No tracking

        scrollToBottom()
        Thread.sleep(1000)
        assertElementCount("#recommendation-container .story-summary-card", 0)
    }
}
