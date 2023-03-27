package com.wutsi.application.membership.onboard.page

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractEndpointTest
import com.wutsi.application.membership.onboard.dto.SubmitProfileRequest
import com.wutsi.application.membership.onboard.entity.OnboardEntity
import com.wutsi.flutter.sdui.Action
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

internal class Onboard02ProfilePageTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private val entity = OnboardEntity(
        phoneNumber = PHONE_NUMBER,
        country = "CM",
        language = "fr",
    )

    private val request = SubmitProfileRequest(
        displayName = "Ray Sponsible",
    )

    private fun url(action: String = "") = "http://localhost:$port/onboard/pages/profile$action"

    @BeforeTest
    override fun setUp() {
        super.setUp()

        doReturn(entity).whenever(cache).get(DEVICE_ID, OnboardEntity::class.java)
    }

    @Test
    fun index() {
        assertEndpointEquals("/membership/onboard/pages/profile.json", url())
    }

    @Test
    fun submit() {
        // GIVEN
        val response = rest.postForEntity(url("/submit"), request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val obj = argumentCaptor<OnboardEntity>()
        verify(cache).put(eq(DEVICE_ID), obj.capture())
        assertEquals(entity.phoneNumber, obj.firstValue.phoneNumber)
        assertEquals(entity.country, obj.firstValue.country)
        assertEquals(entity.language, obj.firstValue.language)
        assertEquals(request.displayName, obj.firstValue.displayName)
    }
}
