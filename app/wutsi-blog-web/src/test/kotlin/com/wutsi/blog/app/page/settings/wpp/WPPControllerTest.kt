package com.wutsi.blog.app.page.settings.wpp

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.WPPConfig
import com.wutsi.blog.user.dto.JoinWPPCommand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Date

class WPPControllerTest : SeleniumTestSupport() {
    @Test
    fun join() {
        // GIVEN
        val user = setupLoggedInUser(100, blog = true, walletId = "wallet-id")

        // WHEN
        navigate(url("/partner"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS_WPP)
        click("#btn-join")

        assertCurrentPageIs(PageName.SETTINGS_WPP_JOIN)
        assertElementNotPresent(".alert-danger")
        assertElementNotPresent(".alert-success")
        click("#btn-join")

        assertCurrentPageIs(PageName.SETTINGS_WPP_SUCCESS)
        val command = argumentCaptor<JoinWPPCommand>()
        verify(userBackend).joinWpp(command.capture())
        assertEquals(user.id, command.firstValue.userId)
    }

    @Test
    fun `non blog cannot join WPP`() {
        // GIVEN
        setupLoggedInUser(100, blog = false, walletId = "wallet-id")

        // WHEN
        navigate(url("/partner"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS_WPP)
        click("#btn-join")

        assertCurrentPageIs(PageName.SETTINGS_WPP_JOIN)
        assertElementNotPresent("#btn-join")
        assertElementPresent(".alert-danger")
        assertElementNotPresent(".alert-success")
    }

    @Test
    fun `blog without monetization cannot join`() {
        // GIVEN
        setupLoggedInUser(100, blog = true, walletId = null)

        // WHEN
        navigate(url("/partner"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS_WPP)
        click("#btn-join")

        assertCurrentPageIs(PageName.SETTINGS_WPP_JOIN)
        assertElementNotPresent("#btn-join")
        assertElementPresent(".alert-danger")
        assertElementNotPresent(".alert-success")
    }

    @Test
    fun `blog with not enough subscriber cannot join`() {
        // GIVEN
        setupLoggedInUser(
            100,
            blog = true,
            walletId = "wallet-id",
            subscriberCount = WPPConfig.MIN_SUBSCRIBER_COUNT - 1
        )

        // WHEN
        navigate(url("/partner"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS_WPP)
        click("#btn-join")

        assertCurrentPageIs(PageName.SETTINGS_WPP_JOIN)
        assertElementNotPresent("#btn-join")
        assertElementPresent(".alert-danger")
        assertElementNotPresent(".alert-success")
    }

    @Test
    fun `blog with not enough stories cannot join`() {
        // GIVEN
        setupLoggedInUser(100, blog = true, walletId = "wallet-id", subscriberCount = WPPConfig.MIN_STORY_COUNT - 1)

        // WHEN
        navigate(url("/partner"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS_WPP)
        click("#btn-join")

        assertCurrentPageIs(PageName.SETTINGS_WPP_JOIN)
        assertElementNotPresent("#btn-join")
        assertElementPresent(".alert-danger")
        assertElementNotPresent(".alert-success")
    }

    @Test
    fun `blog with not old enough`() {
        // GIVEN
        setupLoggedInUser(100, blog = true, walletId = "wallet-id", creationDateTime = Date())

        // WHEN
        navigate(url("/partner"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS_WPP)
        click("#btn-join")

        assertCurrentPageIs(PageName.SETTINGS_WPP_JOIN)
        assertElementNotPresent("#btn-join")
        assertElementPresent(".alert-danger")
        assertElementNotPresent(".alert-success")
    }

    @Test
    fun `already member`() {
        // GIVEN
        setupLoggedInUser(100, blog = true, walletId = "wallet-id", wpp = true)

        // WHEN
        navigate(url("/partner"))

        // THEN
        assertCurrentPageIs(PageName.SETTINGS_WPP)
        click("#btn-join")

        assertCurrentPageIs(PageName.SETTINGS_WPP_JOIN)
        assertElementNotPresent("#btn-join")
        assertElementNotPresent(".alert-danger")
        assertElementPresent(".alert-success")
    }
}
