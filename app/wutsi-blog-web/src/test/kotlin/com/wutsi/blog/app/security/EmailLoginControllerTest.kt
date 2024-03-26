package com.wutsi.blog.app.security

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.account.dto.CreateLoginLinkCommand
import com.wutsi.blog.account.dto.GetLoginLinkResponse
import com.wutsi.blog.account.dto.Link
import com.wutsi.blog.account.dto.LoginUserCommand
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.reader.ReadControllerTest
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
import kotlin.test.assertEquals

class EmailLoginControllerTest : SeleniumTestSupport() {
    private val topics = listOf(
        Topic(id = 100, name = "Topic 100"),
        Topic(id = 101, name = "Topic 101"),
        Topic(id = 102, name = "Topic 102"),
    )

    val user = User(
        id = 111L,
        name = "ray.sponsible",
        email = "ray.sponsible@gmail.com",
    )

    val blog = User(
        id = 111L,
        name = "foo.bar",
    )

    private val story = Story(
        id = user.id,
        userId = blog.id,
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

        doReturn(GetUserResponse(user)).whenever(userBackend).get(user.id)
        doReturn(GetUserResponse(user)).whenever(userBackend).get(user.name)

        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.id)

        doReturn(SearchTopicResponse(topics)).whenever(topicBackend).all()

        doReturn(GetStoryResponse(story)).whenever(storyBackend).get(any())
    }

    @Test
    fun login() {
        navigate(url("/login"))

        input("input[name=email]", "ray.sponsible@gmail.com")
        click("#btn-submit")

        val cmd = argumentCaptor<CreateLoginLinkCommand>()
        verify(authenticationBackend).createLink(cmd.capture())
        assertEquals("ray.sponsible@gmail.com", cmd.firstValue.email)
    }

    @Test
    fun `handle email link`() {
        val link = Link(
            email = user.email!!,
        )
        doReturn(GetLoginLinkResponse(link)).whenever(authenticationBackend).getLink(any())

        navigate(url("/login/email/callback?link-id=111"))

        val cmd = argumentCaptor<LoginUserCommand>()
        verify(authenticationBackend).login(cmd.capture())
        assertEquals(link.email, cmd.firstValue.email)
        assertEquals("email", cmd.firstValue.provider)

        assertCurrentPageIs(PageName.HOME)
    }

    @Test
    fun `handle email link and goto story`() {
        val user = User(
            id = 111L,
            name = "ray.sponsible",
            email = "ray.sponsible@gmail.com",
        )
        doReturn(GetUserResponse(user)).whenever(userBackend).get(111L)
        doReturn(GetUserResponse(user)).whenever(userBackend).get("ray.sponsible")

        val link = Link(
            email = "ray.sponsible@gmail.com",
            storyId = story.id,
            referer = "story",
            redirectUrl = "http://localhost:$port/read/${story.id}/this-is-a-story}"
        )
        doReturn(GetLoginLinkResponse(link)).whenever(authenticationBackend).getLink(any())

        navigate(url("/login/email/callback?link-id=111"))

        val cmd = argumentCaptor<LoginUserCommand>()
        verify(authenticationBackend).login(cmd.capture())
        assertEquals(link.email, cmd.firstValue.email)
        assertEquals("email", cmd.firstValue.provider)

        assertCurrentPageIs(PageName.READ)
    }
}
