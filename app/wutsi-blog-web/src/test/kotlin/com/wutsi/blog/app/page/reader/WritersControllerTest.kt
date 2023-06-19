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

class WritersControllerTest : SeleniumTestSupport() {
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
                        pictureUrl = "https://picsum.photos/200/300",
                    ),
                    UserSummary(id = 2, name = "roger.milla", blog = true, subscriberCount = 10),
                    UserSummary(id = 3, name = "samuel.etoo", blog = true, subscriberCount = 30),
                ),
            ),
        ).whenever(userBackend).search(any())
    }

    @Test
    fun writers() {
        driver.get("$url/writers")
        assertCurrentPageIs(PageName.WRITERS)

        assertElementCount(".author-summary-card", 3)

        assertElementPresent("#author-summary-card-1")
        assertElementAttribute("#author-summary-card-1 a", "href", "/@/ray.sponsible")

        assertElementPresent("#author-summary-card-2")
        assertElementAttribute("#author-summary-card-w a", "href", "/@/roger.milla")

        assertElementPresent("#author-summary-card-3")
        assertElementAttribute("#author-summary-card-3 a", "href", "/@/samuel.etoo")
    }
}
