package com.wutsi.marketplace.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.access.dto.CreateStoreRequest
import com.wutsi.marketplace.access.dto.CreateStoreResponse
import com.wutsi.marketplace.access.dto.GetStoreResponse
import com.wutsi.marketplace.access.dto.Store
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingService
import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateStoreControllerTest : AbstractSecuredController2Test() {
    @MockBean
    private lateinit var messagingServiceProvider: MessagingServiceProvider

    private lateinit var messaging: MessagingService

    protected var store = Store()
    protected var account = Account()

    @BeforeEach
    override fun setUp() {
        super.setUp()

        account = Fixtures.createAccount(
            id = ACCOUNT_ID,
            business = true,
            businessId = 111L,
            email = "ray$ACCOUNT_ID@gmail.com",
            name = "ray$ACCOUNT_ID",
        )
        doReturn(GetAccountResponse(account)).whenever(membershipAccessApi).getAccount(any())

        store = Fixtures.createStore(id = STORE_ID, accountId = AbstractSecuredControllerTest.ACCOUNT_ID)
        doReturn(GetStoreResponse(store)).whenever(marketplaceAccessApi).getStore(any())

        messaging = mock()
        doReturn(messaging).whenever(messagingServiceProvider).get(MessagingType.EMAIL)
    }

    fun url() = "http://localhost:$port/v1/stores"

    @Test
    fun enable() {
        // GIVEN
        doReturn(CreateStoreResponse(STORE_ID)).whenever(marketplaceAccessApi).createStore(any())

        // WHEN
        val response = rest.postForEntity(url(), null, Any::class.java)
        Thread.sleep(10000)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceAccessApi).createStore(
            CreateStoreRequest(
                accountId = account.id,
                businessId = account.businessId!!,
                currency = "XAF",
            ),
        )

        verify(membershipAccessApi).updateAccountAttribute(
            id = account.id,
            request = UpdateAccountAttributeRequest(
                name = "store-id",
                value = STORE_ID.toString(),
            ),
        )

        val message = argumentCaptor<Message>()
        verify(messaging).send(message.capture())
        assertEquals(account.displayName, message.firstValue.recipient.displayName)
        assertEquals(account.email, message.firstValue.recipient.email)
        assertEquals("Welcome to Wutsi community", message.firstValue.subject)
    }

    @Test
    fun `store already created`() {
        // GIVEN
        account = Fixtures.createAccount(
            id = ACCOUNT_ID,
            business = true,
            businessId = 111L,
            email = "ray$ACCOUNT_ID@gmail.com",
            name = "ray$ACCOUNT_ID",
            storeId = 5555L,
        )
        doReturn(GetAccountResponse(account)).whenever(membershipAccessApi).getAccount(any())

        doReturn(CreateStoreResponse(STORE_ID)).whenever(marketplaceAccessApi).createStore(any())

        // WHEN
        val response = rest.postForEntity(url(), null, Any::class.java)
        Thread.sleep(10000)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceAccessApi, never()).createStore(any())
        verify(membershipAccessApi, never()).updateAccountAttribute(any(), any())
        verify(messaging, never()).send(any())
    }

    @Test
    fun countryNotSupported() {
        // GIVEN
        val account = Fixtures.createAccount(country = "CA", business = true)
        doReturn(GetAccountResponse(account)).whenever(membershipAccessApi).getAccount(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(), null, Any::class.java)
        }
        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.STORE_NOT_SUPPORTED_IN_COUNTRY.urn, response.error.code)

        verify(marketplaceAccessApi, never()).createStore(any())

        verify(membershipAccessApi, never()).updateAccountAttribute(any(), any())
    }
}
