package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UserSummary
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HomeControllerTest : SeleniumTestSupport() {
    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            SearchUserResponse(
                users = listOf(
                    UserSummary(
                        id = 1,
                        name = "ray.sponsible",
                        blog = true,
                        subscriberCount = 100,
                        pictureUrl = "https://picsum.photos/200/200",
                    ),
                    UserSummary(
                        id = 2,
                        name = "roger.milla",
                        blog = true,
                        subscriberCount = 10,
                        pictureUrl = "https://picsum.photos/100/100",
                    ),
                    UserSummary(
                        id = 3,
                        name = "samuel.etoo",
                        blog = true,
                        subscriberCount = 30,
                        pictureUrl = "https://picsum.photos/128/128",
                    ),
                ),
            ),
        ).whenever(userBackend).search(any())
    }

    @Test
    fun writers() {
        driver.get("$url")
        assertCurrentPageIs(PageName.HOME)

        assertElementCount(".author-summary-card", 3)

        assertElementPresent("#author-summary-card-1")
        assertElementAttributeEndsWith("#author-summary-card-1 a", "href", "/@/ray.sponsible")
        assertElementAttribute("#author-summary-card-1 img src", "src", "https://picsum.photos/200/200")

        assertElementPresent("#author-summary-card-2")
        assertElementAttributeEndsWith("#author-summary-card-w a", "href", "/@/roger.milla")
        assertElementAttribute("#author-summary-card-2 img src", "src", "https://picsum.photos/100/100")

        assertElementPresent("#author-summary-card-3")
        assertElementAttributeEndsWith("#author-summary-card-3 a", "href", "/@/samuel.etoo")
        assertElementAttribute("#author-summary-card-3 img src", "src", "https://picsum.photos/128/128")

        assertElementAttributeEndsWith("#btn-create-hero", "href", "/create")
        assertElementAttributeEndsWith("#btn-create-bottom", "href", "/create")
    }
}
