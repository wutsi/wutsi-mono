package com.wutsi.marketplace.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.AccountStatus
import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.access.dto.GetStoreResponse
import com.wutsi.marketplace.access.dto.Store
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

abstract class AbstractStoreControllerTest<Req> : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    protected var store = Store()
    protected var account = Account()
    protected var request: Req? = null

    protected abstract fun url(): String
    protected abstract fun createRequest(): Req?

    open fun submit() {
        rest.postForEntity(url(), request, Any::class.java)
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()

        account = Fixtures.createAccount(
            id = ACCOUNT_ID,
            business = true,
            businessId = 111L,
            email = "ray$ACCOUNT_ID@gmail.com",
        )
        doReturn(GetAccountResponse(account)).whenever(membershipAccessApi).getAccount(any())

        store = Fixtures.createStore(id = STORE_ID, accountId = ACCOUNT_ID)
        doReturn(GetStoreResponse(store)).whenever(marketplaceAccessApi).getStore(any())
    }

    @Test
    fun notActive() {
        // GIVEN
        account = Fixtures.createAccount(status = AccountStatus.INACTIVE)
        doReturn(GetAccountResponse(account)).whenever(membershipAccessApi).getAccount(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            submit()
        }
        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.MEMBER_NOT_ACTIVE.urn, response.error.code)

        verify(eventStream, never()).publish(any(), any())
    }
}
