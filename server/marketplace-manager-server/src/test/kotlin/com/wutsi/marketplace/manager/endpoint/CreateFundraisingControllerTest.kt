package com.wutsi.marketplace.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.access.dto.CreateFundraisingRequest
import com.wutsi.marketplace.access.dto.CreateFundraisingResponse
import com.wutsi.marketplace.access.dto.Fundraising
import com.wutsi.marketplace.access.dto.GetFundraisingResponse
import com.wutsi.marketplace.manager.Fixtures
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.core.error.ErrorResponse
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
public class CreateFundraisingControllerTest : AbstractSecuredController2Test() {
    @MockBean
    private lateinit var messagingServiceProvider: MessagingServiceProvider

    private lateinit var messaging: MessagingService

    protected var fundraising = Fundraising()
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

        fundraising =
            Fixtures.createFundraising(id = FUNDRAISING_ID, accountId = AbstractSecuredControllerTest.ACCOUNT_ID)
        doReturn(GetFundraisingResponse(fundraising)).whenever(marketplaceAccessApi).getFundraising(any())

        messaging = mock()
        doReturn(messaging).whenever(messagingServiceProvider).get(MessagingType.EMAIL)
    }

    fun url() = "http://localhost:$port/v1/fundraisings"

    @Test
    fun enable() {
        // GIVEN
        doReturn(CreateFundraisingResponse(FUNDRAISING_ID)).whenever(marketplaceAccessApi).createFundraising(any())

        // WHEN
        val response = rest.postForEntity(url(), null, Any::class.java)
        Thread.sleep(15000)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceAccessApi).createFundraising(
            CreateFundraisingRequest(
                accountId = account.id,
                businessId = account.businessId!!,
                currency = "XAF",
            ),
        )

        verify(membershipAccessApi).updateAccountAttribute(
            id = account.id,
            request = UpdateAccountAttributeRequest(
                name = "fundraising-id",
                value = FUNDRAISING_ID.toString(),
            ),
        )
//        val message = argumentCaptor<Message>()
//        verify(messaging).send(message.capture())
//        assertEquals(account.displayName, message.firstValue.recipient.displayName)
//        assertEquals(account.email, message.firstValue.recipient.email)
//        assertEquals("Welcome to Wutsi community", message.firstValue.subject)
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
        assertEquals(ErrorURN.FUNDRAISING_NOT_SUPPORTED_IN_COUNTRY.urn, response.error.code)

        verify(marketplaceAccessApi, never()).createStore(any())

        verify(membershipAccessApi, never()).updateAccountAttribute(any(), any())
    }
}
