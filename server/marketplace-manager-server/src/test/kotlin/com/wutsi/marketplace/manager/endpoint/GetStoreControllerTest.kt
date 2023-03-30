package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.StoreStatus
import com.wutsi.marketplace.access.dto.GetStoreResponse
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.marketplace.manager.endpoint.AbstractSecuredControllerTest.Companion.ACCOUNT_ID
import com.wutsi.marketplace.manager.endpoint.AbstractSecuredControllerTest.Companion.BUSINESS_ID
import com.wutsi.marketplace.manager.endpoint.AbstractSecuredControllerTest.Companion.STORE_ID
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetStoreControllerTest : AbstractControllerTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port/v1/stores/$STORE_ID"

    @Test
    public fun invoke() {
        // GIVEN
        val store = Fixtures.createStore(
            id = STORE_ID,
            accountId = ACCOUNT_ID,
            status = StoreStatus.ACTIVE,
            businessId = BUSINESS_ID,
        )
        doReturn(GetStoreResponse(store)).whenever(marketplaceAccessApi).getStore(any())

        // WHEN
        val response =
            rest.getForEntity(url(), com.wutsi.marketplace.manager.dto.GetStoreResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceAccessApi).getStore(STORE_ID)

        val prod = response.body!!.store
        assertEquals(store.id, prod.id)
        assertEquals(store.accountId, prod.accountId)
        assertEquals(store.status, prod.status)
        assertEquals(store.businessId, prod.businessId)
        verify(eventStream, never()).publish(any(), any())
    }
}
