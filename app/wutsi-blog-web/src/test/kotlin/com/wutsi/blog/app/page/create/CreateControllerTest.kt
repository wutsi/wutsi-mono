package com.wutsi.blog.app.page.create

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.backend.dto.IpApiResponse
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.user.dto.CreateBlogCommand
import com.wutsi.blog.user.dto.RecommendUserResponse
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UpdateUserAttributeCommand
import com.wutsi.blog.user.dto.UserSummary
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateControllerTest : SeleniumTestSupport() {
    private val users = listOf(
        UserSummary(
            id = 10,
            name = "ray.sponsible",
            blog = true,
            subscriberCount = 100,
            pictureUrl = "https://picsum.photos/200/200",
            biography = "Biography of the user ...",
        ),
        UserSummary(
            id = 20,
            name = "roger.milla",
            blog = true,
            subscriberCount = 10,
            pictureUrl = "https://picsum.photos/100/100",
            biography = "Biography of the user ...",
        ),
        UserSummary(
            id = 30,
            name = "samuel.etoo",
            blog = true,
            subscriberCount = 30,
            pictureUrl = "https://picsum.photos/128/128",
            biography = "Biography of the user ...",
        ),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchUserResponse(users)).whenever(userBackend).search(any())
        doReturn(RecommendUserResponse(users.map { it.id })).whenever(userBackend).recommend(any())
    }

    @Test
    fun create() {
        // GIVEN
        val userId = 1L
        setupLoggedInUser(userId)

        doReturn(IpApiResponse(countryCode = "CM")).whenever(ipApiBackend).resolve(any())

        // Blog name
        driver.get("$url/create")
        assertCurrentPageIs(PageName.CREATE)
        input("input[name=value]", "new-blog")
        click("#btn-next")
        verify(userBackend).updateAttribute(UpdateUserAttributeCommand(userId, "name", "new-blog"))

        // Blog email
        assertCurrentPageIs(PageName.CREATE_EMAIL)
        input("input[name=value]", "new-blog@gmail.com")
        click("#btn-next")
        verify(userBackend).updateAttribute(UpdateUserAttributeCommand(userId, "email", "new-blog@gmail.com"))

        // Country
        assertCurrentPageIs(PageName.CREATE_COUNTRY)
        click("#btn-next")

        // Review
        assertCurrentPageIs(PageName.CREATE_REVIEW)
        assertElementCount("#writer-container .author-suggestion-card", users.size)
        click("#btn-create")

        val cmd = argumentCaptor<CreateBlogCommand>()
        verify(userBackend).createBlog(cmd.capture())
        assertEquals(userId, cmd.firstValue.userId)
        assertEquals(users.map { it.id }.sorted(), cmd.firstValue.subscribeToUserIds.sorted())

        // Success
        assertCurrentPageIs(PageName.CREATE_SUCCESS)
        assertElementPresent("#share-modal a[data-target=facebook]")
        assertElementPresent("#share-modal a[data-target=twitter]")
        click("#btn-next")

        assertCurrentPageIs(PageName.BLOG)
    }

    @Test
    fun createUncheckAllBlogRecommendation() {
        // GIVEN
        val userId = 1L
        setupLoggedInUser(userId)

        doReturn(IpApiResponse(countryCode = "CM")).whenever(ipApiBackend).resolve(any())

        // Blog name
        driver.get("$url/create")
        input("input[name=value]", "new-blog")
        click("#btn-next")

        // Blog email
        input("input[name=value]", "new-blog@gmail.com")
        click("#btn-next")

        // Country
        click("#btn-next")

        // Review
        click("#author-suggestion-card-${users[0].id} input[type=checkbox]")
        click("#author-suggestion-card-${users[1].id} input[type=checkbox]")
        click("#author-suggestion-card-${users[2].id} input[type=checkbox]")
        click("#btn-create")

        val cmd = argumentCaptor<CreateBlogCommand>()
        verify(userBackend).createBlog(cmd.capture())
        assertEquals(userId, cmd.firstValue.userId)
        assertTrue(cmd.firstValue.subscribeToUserIds.isEmpty())

        // Success
        assertCurrentPageIs(PageName.CREATE_SUCCESS)
    }

    @Test
    fun alreadyCreated() {
        // GIVEN
        setupLoggedInUser(1, blog = true)

        // Blog name
        driver.get("$url/create")
        assertCurrentPageIs(PageName.BLOG)
    }
}
