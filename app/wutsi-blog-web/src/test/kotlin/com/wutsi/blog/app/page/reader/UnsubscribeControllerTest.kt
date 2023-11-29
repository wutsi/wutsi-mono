package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.subscription.dto.UnsubscribeCommand
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UnsubscribeControllerTest : SeleniumTestSupport() {
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

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.id)
        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.name)
    }

    @Test
    fun `unsubscribe email`() {
        // WHEN
        val email = "ray.sponsible@gmail.com"
        driver.get("$url/@/${blog.name}/unsubscribe?email=$email")

        assertCurrentPageIs(PageName.UNSUBSCRIBE)
        assertElementAttribute("input[name=email]", "value", email)
        click("#btn-submit")

        val command = argumentCaptor<UnsubscribeCommand>()
        verify(subscriptionBackend).unsubscribe(command.capture())
        assertEquals(email, command.firstValue.email)
        assertEquals(blog.id, command.firstValue.userId)
        assertEquals(-1, command.firstValue.subscriberId)

        assertCurrentPageIs(PageName.UNSUBSCRIBED)
    }

    @Test
    fun `unsubscribe logged in`() {
        // GIVEN
        val email = "ray.sponsible@gmail.com"
        setupLoggedInUser(55, email = email)

        // WHEN
        driver.get("$url/@/${blog.name}/unsubscribe")

        assertCurrentPageIs(PageName.UNSUBSCRIBE)
        assertElementAttribute("input[name=email]", "value", "")
        input("input[name=email]", email)
        click("#btn-submit")

        val command = argumentCaptor<UnsubscribeCommand>()
        verify(subscriptionBackend).unsubscribe(command.capture())
        assertEquals(email, command.firstValue.email)
        assertEquals(blog.id, command.firstValue.userId)
        assertEquals(-1, command.firstValue.subscriberId)

        assertCurrentPageIs(PageName.UNSUBSCRIBED)
    }

    @Test
    fun `unsubscribe anonymous`() {
        // GIVEN
        val email = "ray.sponsible@gmail.com"

        // WHEN
        driver.get("$url/@/${blog.name}/unsubscribe")

        assertCurrentPageIs(PageName.UNSUBSCRIBE)
        input("input[name=email]", email)
        click("#btn-submit")

        val command = argumentCaptor<UnsubscribeCommand>()
        verify(subscriptionBackend).unsubscribe(command.capture())
        assertEquals(email, command.firstValue.email)
        assertEquals(blog.id, command.firstValue.userId)
        assertEquals(-1, command.firstValue.subscriberId)

        assertCurrentPageIs(PageName.UNSUBSCRIBED)
    }
}
