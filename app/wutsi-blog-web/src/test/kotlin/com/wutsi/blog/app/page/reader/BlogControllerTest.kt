package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.User
import com.wutsi.blog.user.dto.UserSummary
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException

class BlogControllerTest : SeleniumTestSupport() {
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

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.id)
        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.name)
    }

    @Test
    fun about() {
        driver.get("$url/@/${blog.name}/about")

        assertCurrentPageIs(PageName.BLOG_ABOUT)

        assertElementText("h1", "About ${blog.name}")
        assertElementPresent("a.btn-follow")
    }

    @Test
    fun blog() {
        // GIVEN
        doReturn(
            // Popular Stories
            SearchStoryResponse(
                stories = listOf(
                    StorySummary(
                        id = 100,
                        userId = blog.id,
                        title = "Story 1",
                        thumbnailUrl = "https://picsum.photos/400/400",
                        commentCount = 11,
                        likeCount = 12,
                        shareCount = 13,
                    ),
                    StorySummary(
                        id = 200,
                        userId = blog.id,
                        title = "Story 2",
                        thumbnailUrl = "https://picsum.photos/400/400",
                        commentCount = 20,
                        likeCount = 21,
                        shareCount = 22,
                    ),
                    StorySummary(
                        id = 200,
                        userId = blog.id,
                        title = "Story 3",
                        thumbnailUrl = "https://picsum.photos/400/400",
                    ),
                ),
            ),
        ).whenever(storyBackend).search(any())

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

        // WHEN
        driver.get("$url/@/${blog.name}")

        assertCurrentPageIs(PageName.BLOG)

        assertElementText("h1", "${blog.name}")
        assertElementPresent("#story-card-100")
        assertElementPresent("#story-card-200")
        assertElementPresent("#story-card-200")
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
}
