package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LogoutControllerTest : SeleniumTestSupport() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        setupLoggedInUser(1)
    }

    @Test
    fun logout() {
        driver.get("$url/logout")
        verify(authenticationBackend).logout(any())

        assertCurrentPageIs(PageName.HOME)
    }
}
