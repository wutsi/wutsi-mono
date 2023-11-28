package com.wutsi.blog.app.page.create

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.backend.dto.IpApiResponse
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiResponse
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.dto.UserKpi
import com.wutsi.blog.user.dto.CreateBlogCommand
import com.wutsi.blog.user.dto.RecommendUserResponse
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UpdateUserAttributeCommand
import com.wutsi.blog.user.dto.UserSummary
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Locale
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
            language = "en",
        ),
        UserSummary(
            id = 20,
            name = "roger.milla",
            blog = true,
            subscriberCount = 10,
            pictureUrl = "https://picsum.photos/100/100",
            biography = "Biography of the user ...",
            language = "en",
        ),
        UserSummary(
            id = 30,
            name = "samuel.etoo",
            blog = true,
            subscriberCount = 30,
            pictureUrl = "https://picsum.photos/128/128",
            biography = "Biography of the user ...",
            language = "en",
        ),
    )

    private val userKpis = listOf(
        UserKpi(userId = 10, type = KpiType.READ, source = TrafficSource.EMAIL, year = 2020, month = 1, value = 100),
        UserKpi(userId = 20, type = KpiType.READ, source = TrafficSource.DIRECT, year = 2020, month = 2, value = 110),
        UserKpi(userId = 30, type = KpiType.READ, source = TrafficSource.LINKEDIN, year = 2020, month = 3, value = 120),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchUserKpiResponse(userKpis)).whenever(kpiBackend).search(any<SearchUserKpiRequest>())
        doReturn(SearchUserResponse(users)).whenever(userBackend).search(any())
        doReturn(RecommendUserResponse(users.map { it.id })).whenever(userBackend).recommend(any())
    }

    @Test
    fun create() {
        // GIVEN
        val userId = 1L
        setupLoggedInUser(userId, language = "en")

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

        // Language
        assertCurrentPageIs(PageName.CREATE_LANGUAGE)
        select("select[name=value]", languageIndex("en"))
        click("#btn-next")

        // Review
        assertCurrentPageIs(PageName.CREATE_REVIEW)
        click("#chk-writer-${users[0].id}")
        click("#chk-writer-${users[1].id}")
        assertElementCount("#writer-container .author-suggestion-card", users.size)
        click("#btn-create")

        val cmd = argumentCaptor<CreateBlogCommand>()
        verify(userBackend).createBlog(cmd.capture())
        assertEquals(userId, cmd.firstValue.userId)
        assertEquals(
            listOf(users[0].id, users[1].id).sorted(),
            cmd.firstValue.subscribeToUserIds.sorted()
        )

        // Success
        assertCurrentPageIs(PageName.CREATE_SUCCESS)
        assertElementPresent("#share-modal a[data-target=facebook]")
        assertElementPresent("#share-modal a[data-target=twitter]")
        click("#btn-next")

        assertCurrentPageIs(PageName.BLOG)
    }

    @Test
    fun createNoBlogRecommendation() {
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

        // Language
        assertCurrentPageIs(PageName.CREATE_LANGUAGE)
        select("select[name=value]", languageIndex("en"))
        click("#btn-next")

        // Review
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

    @Test
    fun nameWithSpace() {
        // GIVEN
        val userId = 1L
        setupLoggedInUser(userId, language = "en")

        doReturn(IpApiResponse(countryCode = "CM")).whenever(ipApiBackend).resolve(any())

        // Blog name
        driver.get("$url/create")
        assertCurrentPageIs(PageName.CREATE)
        input("input[name=value]", "NEW blog")
        click("#btn-next")
        verify(userBackend).updateAttribute(UpdateUserAttributeCommand(userId, "name", "new-blog"))

        assertCurrentPageIs(PageName.CREATE_EMAIL)
    }

    private fun languageIndex(language: String): Int =
        Locale.getISOLanguages()
            .map { lang -> Locale(lang) }
            .sortedBy { it.displayLanguage }
            .indexOf(Locale(language))
}
