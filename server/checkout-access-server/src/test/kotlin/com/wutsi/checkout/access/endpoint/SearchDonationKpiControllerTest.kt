package com.wutsi.checkout.access.endpoint

import com.wutsi.checkout.access.dto.SearchDonationKpiRequest
import com.wutsi.checkout.access.dto.SearchDonationKpiResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchDonationKpiController.sql"])
public class SearchDonationKpiControllerTest {
    @LocalServerPort
    public val port: Int = 0

    private val rest = RestTemplate()

    @Test
    public fun byBusiness() {
        val request = SearchDonationKpiRequest(
            businessId = 1,
        )
        val response = rest.postForEntity(url(), request, SearchDonationKpiResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val kpis = response.body!!.kpis
        assertEquals(4, kpis.size)

        assertEquals(11, kpis[0].totalDonations)
        assertEquals(50000, kpis[0].totalValue)

        assertEquals(3, kpis[1].totalDonations)
        assertEquals(3000, kpis[1].totalValue)

        assertEquals(1, kpis[2].totalDonations)
        assertEquals(2000, kpis[2].totalValue)

        assertEquals(5, kpis[3].totalDonations)
        assertEquals(30000, kpis[3].totalValue)
    }

    @Test
    public fun aggregateByBusiness() {
        val request = SearchDonationKpiRequest(
            businessId = 1,
            aggregate = true,
        )
        val response = rest.postForEntity(url(), request, SearchDonationKpiResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val kpis = response.body!!.kpis
        assertEquals(1, kpis.size)

        assertEquals(20, kpis[0].totalDonations)
        assertEquals(85000, kpis[0].totalValue)
    }

    @Test
    public fun byDates() {
        val request = SearchDonationKpiRequest(
            businessId = 1,
            fromDate = LocalDate.now().minusDays(1),
            toDate = LocalDate.now(),
        )
        val response = rest.postForEntity(url(), request, SearchDonationKpiResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val kpis = response.body!!.kpis
        assertEquals(2, kpis.size)

        assertEquals(1, kpis[0].totalDonations)
        assertEquals(2000, kpis[0].totalValue)

        assertEquals(5, kpis[1].totalDonations)
        assertEquals(30000, kpis[1].totalValue)
    }

    private fun url() = "http://localhost:$port/v1/kpis/donations/search"
}
