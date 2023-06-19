package com.wutsi.blog.app.page.reader

import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.jupiter.api.Test

class LegalControllerTest : SeleniumTestSupport() {
    @Test
    fun about() {
        driver.get("$url/about")
        assertCurrentPageIs(PageName.LEGAL_ABOUT)
    }

    @Test
    fun privacy() {
        driver.get("$url/privacy")
        assertCurrentPageIs(PageName.LEGAL_PRIVACY)
    }

    @Test
    fun terms() {
        driver.get("$url/terms")
        assertCurrentPageIs(PageName.LEGAL_TERMS)
    }
}
