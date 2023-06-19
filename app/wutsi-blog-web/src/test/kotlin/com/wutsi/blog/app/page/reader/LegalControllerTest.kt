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
}
