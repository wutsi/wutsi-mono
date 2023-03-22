package com.wutsi.membership.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.membership.manager.Fixtures
import com.wutsi.membership.manager.dto.GetMemberResponse
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetMemberControllerTest : AbstractController2Test() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun getMember() {
        // GIVEN
        val accountId = 111L
        val account = Fixtures.createAccount(
            id = accountId,
            business = true,
            storeId = 1L,
            businessId = 22L,
            name = "ray.sponsible",
        )
        doReturn(GetAccountResponse(account)).whenever(membershipAccess).getAccount(accountId)

        // WHEN
        val response = RestTemplate().getForEntity(url(accountId), GetMemberResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val member = response.body!!.member
        assertEquals(account.id, member.id)
        assertEquals(account.name, member.name)
        assertEquals(account.displayName, member.displayName)
        assertEquals(account.email, member.email)
        assertEquals(account.country, member.country)
        assertEquals(account.business, member.business)
        assertEquals(account.storeId, member.storeId)
        assertEquals(account.businessId, member.businessId)
        assertEquals(account.category?.id, member.category?.id)
        assertEquals(account.category?.title, member.category?.title)
        assertEquals(account.city?.id, member.city?.id)
        assertEquals(account.city?.name, member.city?.name)
    }

    @Test
    fun notFound() {
        // GIVEN
        val e = createFeignNotFoundException(ErrorURN.ACCOUNT_NOT_FOUND.urn)
        doThrow(e).whenever(membershipAccess).getAccount(any())

        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url(3L), GetMemberResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(com.wutsi.error.ErrorURN.MEMBER_NOT_FOUND.urn, response.error.code)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/members/$id"
}
