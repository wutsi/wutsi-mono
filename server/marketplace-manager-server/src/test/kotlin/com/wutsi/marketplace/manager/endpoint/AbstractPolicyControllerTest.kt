package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.marketplace.access.dto.GetStoreResponse
import com.wutsi.marketplace.access.dto.Store
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.access.dto.GetAccountResponse
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.web.server.LocalServerPort

abstract class AbstractPolicyControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    protected var store = Store()
    protected var account = Account()

    @BeforeEach
    override fun setUp() {
        super.setUp()

        account = Fixtures.createAccount(id = ACCOUNT_ID, business = true, storeId = STORE_ID)
        doReturn(GetAccountResponse(account)).whenever(membershipAccessApi).getAccount(any())

        store = Fixtures.createStore(id = STORE_ID, accountId = ACCOUNT_ID)
        doReturn(GetStoreResponse(store)).whenever(marketplaceAccessApi).getStore(any())
    }
}
