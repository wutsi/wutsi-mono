package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.page.SeleniumTestSupport
import org.junit.jupiter.api.Test

class LoginControllerTest : SeleniumTestSupport() {
    @Test
    fun loginPage() {
        navigate("$url/login")

        assertElementNotPresent(".alert-danger")

        assertElementNotPresent(".return")

        assertElementPresent("#login-panel")

        validateButton("google")
        validateButton("facebook")
        validateButton("github")
        validateButton("twitter")
        validateButton("linkedin")
        validateButton("yahoo")
    }

    @Test
    fun loginWithError() {
        navigate("$url/login?error=Failed")

        assertElementPresent(".alert-danger")
    }

    @Test
    fun loginWithReturnUrl() {
        navigate("$url/login?return=https://www.google.ca")

        assertElementAttribute("a.return", "href", "https://www.google.ca")
    }

    private fun validateButton(name: String) {
        assertElementAttributeEndsWith("#btn-$name", "href", "/login/$name")
    }
}
