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
import com.wutsi.membership.manager.dto.SearchPlaceResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class Business03CityPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = BusinessEntity(
        displayName = "Maison H",
        categoryId = 22L,
        cityId = 100L,
    )

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsBusinessUrl()}/pages/city$action"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, BusinessEntity::class.java)

        val cities = listOf(
            Fixtures.createPlaceSummary(100L, "Yaounde"),
            Fixtures.createPlaceSummary(200L, "Douala"),
        )
        doReturn(SearchPlaceResponse(cities)).whenever(membershipManagerApi).searchPlace(any())
    }

    @Test
    fun index() = assertEndpointEquals("/membership/settings/business/pages/city.json", url())

    @Test
    fun submit() {
        // WHEN
        val request = SubmitBusinessAttributeRequest("200")
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/4", action.url)

        verify(cache).put(
            DEVICE_ID,
            BusinessEntity(
                displayName = entity.displayName,
                categoryId = entity.categoryId,
                cityId = request.value.toLong(),
            ),
        )
    }
}
