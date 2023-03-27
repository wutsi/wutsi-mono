package com.wutsi.application.membership.settings.business.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.application.membership.settings.business.dto.SubmitBusinessAttributeRequest
import com.wutsi.application.membership.settings.business.entity.BusinessEntity
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.security.manager.dto.CreateOTPRequest
import com.wutsi.security.manager.dto.CreateOTPResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

internal class Business05EmailPageTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = BusinessEntity(
        displayName = "Maison H",
        categoryId = 22L,
        cityId = 100L,
        whatsapp = true,
    )

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsBusinessUrl()}/pages/email$action"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, BusinessEntity::class.java)
    }

    @Test
    fun index() = assertEndpointEquals("/membership/settings/business/pages/email.json", url())

    @Test
    fun submit() {
        // GIVEN
        val otpToken = "xxx"
        doReturn(CreateOTPResponse(otpToken)).whenever(securityManagerApi).createOtp(any())

        // WHEN
        val request = SubmitBusinessAttributeRequest("ray.sponsible@gmail.com")
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body!!
        assertEquals(ActionType.Page, action.type)
        assertEquals("page:/${Business05EmailPage.PAGE_INDEX + 1}", action.url)

        verify(securityManagerApi).createOtp(
            request = CreateOTPRequest(
                address = request.value,
                type = MessagingType.EMAIL.name,
            ),
        )

        verify(cache).put(
            DEVICE_ID,
            BusinessEntity(
                displayName = entity.displayName,
                categoryId = entity.categoryId,
                cityId = entity.cityId,
                whatsapp = entity.whatsapp,
                email = request.value,
                otpToken = otpToken,
            ),
        )
    }
}
