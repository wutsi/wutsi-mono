package com.wutsi.membership.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.checkout.access.dto.CreatePaymentMethodRequest
import com.wutsi.checkout.access.dto.SearchPaymentProviderResponse
import com.wutsi.enums.PaymentMethodType
import com.wutsi.membership.access.dto.CreateAccountRequest
import com.wutsi.membership.access.dto.CreateAccountResponse
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.membership.manager.Fixtures
import com.wutsi.membership.manager.dto.CreateMemberRequest
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.security.manager.dto.CreatePasswordRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateMemberControllerTest : AbstractController2Test() {
    @LocalServerPort
    val port: Int = 0

    private val request = CreateMemberRequest(
        phoneNumber = "+237670000010",
        displayName = "Ray Sponsible",
        pin = "123456",
        cityId = 111L,
        country = "CM",
    )

    @Test
    fun register() {
        // GIVEN
        val accountId = 111L
        doReturn(CreateAccountResponse(accountId)).whenever(membershipAccess).createAccount(any())

        val account = Fixtures.createAccount(id = accountId, phoneNumber = request.phoneNumber)
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccount(any())

        val provider = Fixtures.createPaymentProviderSummary(id = 11, type = PaymentMethodType.MOBILE_MONEY)
        doReturn(SearchPaymentProviderResponse(listOf(provider))).whenever(checkoutAccessApi)
            .searchPaymentProvider(any())

        // WHEN
        val response = rest.postForEntity(url(), request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val req = argumentCaptor<CreateAccountRequest>()
        verify(membershipAccess).createAccount(req.capture())
        assertEquals(request.phoneNumber, req.firstValue.phoneNumber)
        assertEquals(request.displayName, req.firstValue.displayName)
        assertEquals(request.cityId, req.firstValue.cityId)
        assertEquals("CM", req.firstValue.country)
        assertEquals(language, req.firstValue.language)

        Thread.sleep(10000) // Wait for async
        verify(securityManagerApi).createPassword(
            CreatePasswordRequest(
                accountId = accountId,
                username = request.phoneNumber,
                value = request.pin,
            ),
        )
        verify(checkoutAccessApi).createPaymentMethod(
            CreatePaymentMethodRequest(
                accountId = accountId,
                country = request.country,
                type = PaymentMethodType.MOBILE_MONEY.name,
                number = request.phoneNumber,
                ownerName = request.displayName,
                providerId = provider.id,
            ),
        )
    }

    @Test
    fun registerWithoutPaymentProvider() {
        // GIVEN
        val accountId = 111L
        doReturn(CreateAccountResponse(accountId)).whenever(membershipAccess).createAccount(any())

        doReturn(SearchPaymentProviderResponse(listOf())).whenever(checkoutAccessApi)
            .searchPaymentProvider(any())

        // WHEN
        val response = rest.postForEntity(url(), request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val req = argumentCaptor<CreateAccountRequest>()
        verify(membershipAccess).createAccount(req.capture())
        assertEquals(request.phoneNumber, req.firstValue.phoneNumber)
        assertEquals(request.displayName, req.firstValue.displayName)
        assertEquals(request.cityId, req.firstValue.cityId)
        assertEquals("CM", req.firstValue.country)
        assertEquals(language, req.firstValue.language)

        Thread.sleep(10000) // Wait for async
        verify(securityManagerApi).createPassword(
            CreatePasswordRequest(
                accountId = accountId,
                username = request.phoneNumber,
                value = request.pin,
            ),
        )
        verify(checkoutAccessApi, never()).createPaymentMethod(any())
    }

    @Test
    fun registerWithMultiplePaymentProvider() {
        // GIVEN
        val accountId = 111L
        doReturn(CreateAccountResponse(accountId)).whenever(membershipAccess).createAccount(any())

        doReturn(
            SearchPaymentProviderResponse(
                listOf(
                    Fixtures.createPaymentProviderSummary(id = 111),
                    Fixtures.createPaymentProviderSummary(id = 222),
                ),
            ),
        ).whenever(checkoutAccessApi)
            .searchPaymentProvider(any())

        // WHEN
        val response = rest.postForEntity(url(), request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val req = argumentCaptor<CreateAccountRequest>()
        verify(membershipAccess).createAccount(req.capture())
        assertEquals(request.phoneNumber, req.firstValue.phoneNumber)
        assertEquals(request.displayName, req.firstValue.displayName)
        assertEquals(request.cityId, req.firstValue.cityId)
        assertEquals("CM", req.firstValue.country)
        assertEquals(language, req.firstValue.language)

        Thread.sleep(10000) // Wait for async
        verify(securityManagerApi).createPassword(
            CreatePasswordRequest(
                accountId = accountId,
                username = request.phoneNumber,
                value = request.pin,
            ),
        )
        verify(checkoutAccessApi, never()).createPaymentMethod(any())
    }

    @Test
    fun testAlreadyAssigned() {
        // GIVEN
        val e = createFeignNotFoundException(ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn)
        doThrow(e).whenever(membershipAccess).createAccount(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(), request, Any::class.java)
        }

        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(com.wutsi.error.ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn, response.error.code)

        verify(checkoutAccessApi, never()).createPaymentMethod(any())
        verify(securityManagerApi, never()).createPassword(any())
    }

    private fun url() = "http://localhost:$port/v1/members"
}
