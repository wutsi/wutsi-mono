package com.wutsi.membership.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.AccountStatus
import com.wutsi.membership.access.dto.SearchAccountRequest
import com.wutsi.membership.access.dto.SearchAccountResponse
import com.wutsi.membership.manager.Fixtures
import com.wutsi.membership.manager.dto.SearchMemberRequest
import com.wutsi.membership.manager.dto.SearchMemberResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SearchMemberControllerTest : AbstractControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    public fun invoke() {
        // GIVEN
        val account1 = Fixtures.createAccountSummary()
        val account2 = Fixtures.createAccountSummary()
        doReturn(SearchAccountResponse(listOf(account1, account2))).whenever(membershipAccess).searchAccount(any())

        // WHEN
        val request = SearchMemberRequest(
            phoneNumber = "+237990000001",
            limit = 3,
            offset = 100,
            store = true,
            business = true,
            cityId = 1111,
        )
        val response = rest.postForEntity(url(), request, SearchMemberResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(membershipAccess).searchAccount(
            request = SearchAccountRequest(
                phoneNumber = request.phoneNumber,
                limit = request.limit,
                offset = request.offset,
                status = AccountStatus.ACTIVE.name,
                store = request.store,
                business = request.business,
                cityId = request.cityId,
            ),
        )

        val members = response.body!!.members
        assertEquals(2, members.size)
    }

    private fun url() = "http://localhost:$port/v1/members/search"
}
