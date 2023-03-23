package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.StoreStatus
import com.wutsi.marketplace.access.dto.UpdateStoreStatusRequest
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeactivateStoreControllerTest : AbstractSecuredController2Test() {
    private fun url() = "http://localhost:$port/v1/stores"

    @Test
    fun disable() {
        // GIVEN
        val account = Fixtures.createAccount(id = ACCOUNT_ID, business = true, storeId = STORE_ID)
        doReturn(GetAccountResponse(account)).whenever(membershipAccessApi).getAccount(any())

        // WHEN
        rest.delete(url())
        Thread.sleep(10000)

        // THEN
        verify(marketplaceAccessApi).updateStoreStatus(
            account.storeId!!,
            UpdateStoreStatusRequest(
                status = StoreStatus.INACTIVE.name,
            ),
        )

        verify(membershipAccessApi).updateAccountAttribute(
            id = account.id,
            request = UpdateAccountAttributeRequest(
                name = "store-id",
                value = null,
            ),
        )
    }

    @Test
    fun noStore() {
        // GIVEN
        val account = Fixtures.createAccount(id = ACCOUNT_ID, business = true, storeId = null)
        doReturn(GetAccountResponse(account)).whenever(membershipAccessApi).getAccount(any())

        // WHEN
        rest.delete(url())

        // THEN
        verify(marketplaceAccessApi, never()).createStore(any())

        verify(membershipAccessApi, never()).updateAccountAttribute(any(), any())
    }
}
