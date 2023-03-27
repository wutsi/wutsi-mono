package com.wutsi.application.marketplace.settings.policy.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.application.marketplace.settings.policy.dto.SubmitAttributeRequest
import com.wutsi.marketplace.manager.dto.GetStoreResponse
import com.wutsi.marketplace.manager.dto.Store
import com.wutsi.marketplace.manager.dto.UpdateStorePolicyAttributeRequest
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

internal class SettingsV2PolicyEditorScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var store: Store

    @BeforeEach
    override fun setUp() {
        super.setUp()

        member = Fixtures.createMember(id = 11L, storeId = 1111L, business = true)
        doReturn(GetMemberResponse(member)).whenever(membershipManagerApi).getMember(any())

        store = Fixtures.createStore(id = member.storeId!!, accountId = member.id)
        doReturn(GetStoreResponse(store)).whenever(marketplaceManagerApi).getStore(any())
    }

    @Test
    fun cancellationWindow() =
        assertEndpointEquals(
            "/marketplace/settings/policy/screens/editor-cancellation-window.json",
            url("cancellation-window"),
        )

    @Test
    fun cancellationMessage() =
        assertEndpointEquals(
            "/marketplace/settings/policy/screens/editor-cancellation-message.json",
            url("cancellation-message"),
        )

    @Test
    fun returnContactWindow() =
        assertEndpointEquals(
            "/marketplace/settings/policy/screens/editor-return-contact-window.json",
            url("return-contact-window"),
        )

    @Test
    fun returnShipBackWindow() =
        assertEndpointEquals(
            "/marketplace/settings/policy/screens/editor-return-ship-back-window.json",
            url("return-ship-back-window"),
        )

    @Test
    fun returnMessage() =
        assertEndpointEquals(
            "/marketplace/settings/policy/screens/editor-return-message.json",
            url("return-message"),
        )

    @Test
    fun submit() {
        val name = "return-message"
        val request = SubmitAttributeRequest("Yo man")
        val response = rest.postForEntity(url(name, "/submit"), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceManagerApi).updateStorePolicyAttribute(
            store.id,
            UpdateStorePolicyAttributeRequest(name, request.value),
        )
    }

    private fun url(name: String, action: String = "") =
        "http://localhost:$port${Page.getSettingsPoliciesEditorUrl()}$action?name=$name"
}
