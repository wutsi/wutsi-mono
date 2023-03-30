package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.FundraisingStatus
import com.wutsi.marketplace.access.dto.GetFundraisingResponse
import com.wutsi.marketplace.manager.Fixtures
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetFundraisingControllerTest : AbstractControllerTest() {
    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port/v1/fundraisings/${AbstractSecuredControllerTest.STORE_ID}"

    @Test
    public fun invoke() {
        // GIVEN
        val fundraising = Fixtures.createFundraising(
            id = AbstractSecuredControllerTest.STORE_ID,
            accountId = AbstractSecuredControllerTest.ACCOUNT_ID,
            businessId = AbstractSecuredControllerTest.BUSINESS_ID,
            status = FundraisingStatus.ACTIVE,
        )
        doReturn(GetFundraisingResponse(fundraising)).whenever(marketplaceAccessApi).getFundraising(any())

        // WHEN
        val response =
            rest.getForEntity(url(), com.wutsi.marketplace.manager.dto.GetFundraisingResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(marketplaceAccessApi).getFundraising(AbstractSecuredControllerTest.STORE_ID)

        val prod = response.body!!.fundraising
        assertEquals(fundraising.id, prod.id)
        assertEquals(fundraising.accountId, prod.accountId)
        assertEquals(fundraising.businessId, prod.businessId)
        assertEquals(fundraising.status, prod.status)

        verify(eventStream, never()).publish(any(), any())
    }
}
