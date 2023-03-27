package com.wutsi.application.marketplace.settings.policy.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.marketplace.manager.dto.GetStoreResponse
import com.wutsi.marketplace.manager.dto.Store
import com.wutsi.marketplace.manager.dto.UpdateStorePolicyAttributeRequest
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.assertEquals

internal class SettingsV2PolicyScreenTest : AbstractSecuredEndpointTest() {
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
    fun index() {
        val url = "http://localhost:$port${Page.getSettingsPoliciesUrl()}"
        assertEndpointEquals("/marketplace/settings/policy/screens/policies.json", url)
    }

    @Test
    fun toggleCancellation() {
        val url = "http://localhost:$port${Page.getSettingsPoliciesUrl()}/toggle?name=cancellation-accepted&value=true"
        val response = rest.postForEntity(url, null, Action::class.java)

        verify(marketplaceManagerApi).updateStorePolicyAttribute(
            id = store.id,
            request = UpdateStorePolicyAttributeRequest(
                name = "cancellation-accepted",
                value = "false",
            ),
        )
        val action = response.body!!
        assertEquals("http://localhost:0" + Page.getSettingsPoliciesUrl(), action.url)
        assertEquals(true, action.replacement)
    }

    @Test
    fun toggleReturn() {
        val url = "http://localhost:$port${Page.getSettingsPoliciesUrl()}/toggle?name=return-accepted&value=false"
        val response = rest.postForEntity(url, null, Action::class.java)

        verify(marketplaceManagerApi).updateStorePolicyAttribute(
            id = store.id,
            request = UpdateStorePolicyAttributeRequest(
                name = "return-accepted",
                value = "true",
            ),
        )
        val action = response.body!!
        assertEquals("http://localhost:0" + Page.getSettingsPoliciesUrl(), action.url)
        assertEquals(true, action.replacement)
    }
}
