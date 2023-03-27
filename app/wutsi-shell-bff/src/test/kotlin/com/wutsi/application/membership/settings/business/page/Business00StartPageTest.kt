package com.wutsi.application.membership.settings.business.page

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.application.membership.settings.business.dto.SubmitBusinessAttributeRequest
import com.wutsi.application.membership.settings.business.entity.BusinessEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

internal class Business00StartPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url(action: String = "") = "http://localhost:$port${Page.getSettingsBusinessUrl()}/pages/start$action"

    @Test
    fun index() = assertEndpointEquals("/membership/settings/business/pages/start.json", url())

    @Test
    fun submit() {
        // WHEN
        val request = SubmitBusinessAttributeRequest("Yo man")
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        Assertions.assertEquals(ActionType.Page, action.type)
        Assertions.assertEquals("page:/1", action.url)

        verify(cache).put(
            DEVICE_ID,
            BusinessEntity(
                displayName = member.displayName,
                categoryId = member.category?.id,
                cityId = member.city?.id,
                whatsapp = true,
                biography = member.biography,
                email = member.email!!,
            ),
        )
    }
}
