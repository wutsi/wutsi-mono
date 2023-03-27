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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.assertEquals

internal class Business01DisplayNamePageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = BusinessEntity(
        displayName = "Maison H",
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, BusinessEntity::class.java)

        val categories = listOf(
            Fixtures.createProductCategorySummary(1, "Art"),
            Fixtures.createProductCategorySummary(2, "Bakery"),
        )
        doReturn(SearchCategoryResponse(categories)).whenever(membershipManagerApi).searchCategory(any())
    }

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsBusinessUrl()}/pages/display-name$action"

    @Test
    fun index() {
        assertEndpointEquals("/membership/settings/business/pages/display-name.json", url())
    }

    @Test
    fun submit() {
        // WHEN
        val request = SubmitBusinessAttributeRequest("Yo man")
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/2", action.url)

        verify(cache).put(
            DEVICE_ID,
            BusinessEntity(
                displayName = request.value,
            ),
        )
    }
}
