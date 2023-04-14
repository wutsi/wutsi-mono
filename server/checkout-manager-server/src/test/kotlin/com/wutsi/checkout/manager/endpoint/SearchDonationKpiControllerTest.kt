package com.wutsi.checkout.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.checkout.access.dto.SearchDonationKpiResponse
import com.wutsi.checkout.manager.Fixtures
import com.wutsi.checkout.manager.dto.SearchDonationKpiRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SearchDonationKpiControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    public fun invoke() {
        // GIVEN
        val kpis = listOf(
            Fixtures.createDonationKpiSummary(date = LocalDate.now()),
            Fixtures.createDonationKpiSummary(date = LocalDate.now().minusDays(1)),
            Fixtures.createDonationKpiSummary(date = LocalDate.now().minusDays(3)),
        )
        doReturn(SearchDonationKpiResponse(kpis)).whenever(checkoutAccess).searchDonationKpi(any())

        // GIVEN
        val request = SearchDonationKpiRequest(
            businessId = 1,
            fromDate = LocalDate.now().minusDays(28),
            toDate = LocalDate.now(),
            aggregate = true,
        )
        val response = rest.postForEntity(url(), request, SearchDonationKpiResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(checkoutAccess).searchDonationKpi(
            request = com.wutsi.checkout.access.dto.SearchDonationKpiRequest(
                businessId = request.businessId,
                fromDate = request.fromDate,
                toDate = request.toDate,
                aggregate = request.aggregate,
            ),
        )

        assertEquals(response.body!!.kpis.size, kpis.size)
    }

    private fun url() = "http://localhost:$port/v1/kpis/donations/search"
}
