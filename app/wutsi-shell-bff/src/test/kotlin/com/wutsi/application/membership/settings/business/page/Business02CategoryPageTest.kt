package com.wutsi.application.membership.settings.business.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.application.membership.settings.business.dto.SubmitBusinessAttributeRequest
import com.wutsi.application.membership.settings.business.entity.BusinessEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.membership.manager.dto.SearchCategoryResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class Business02CategoryPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = BusinessEntity(
        displayName = "Maison H",
        categoryId = 22L,
    )

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsBusinessUrl()}/pages/category$action"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, BusinessEntity::class.java)

        val categories = listOf(
            Fixtures.createProductCategorySummary(11L, "Art"),
            Fixtures.createProductCategorySummary(22L, "Bakery"),
        )
        doReturn(SearchCategoryResponse(categories)).whenever(membershipManagerApi).searchCategory(any())
    }

    @Test
    fun index() = assertEndpointEquals("/membership/settings/business/pages/category.json", url())

    @Test
    fun submit() {
        // WHEN
        val request = SubmitBusinessAttributeRequest("11")
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/3", action.url)

        verify(cache).put(
            DEVICE_ID,
            BusinessEntity(
                displayName = entity.displayName,
                categoryId = request.value.toLong(),
            ),
        )
    }
}
