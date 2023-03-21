package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.dto.SearchReservationRequest
import com.wutsi.marketplace.access.dto.SearchReservationResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchReservationController.sql"])
public class SearchReservationControllerTest {
    @LocalServerPort
    public val port: Int = 0

    private val rest = RestTemplate()

    @Test
    fun search() {
        // WHEN
        val request = SearchReservationRequest(
            orderId = "100",
        )
        val response = rest.postForEntity(url(), request, SearchReservationResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val reservationId = response.body!!.reservations.map { it.id }
        assertEquals(2, reservationId.size)
        assertEquals(listOf(100L, 101L), reservationId)
    }

    private fun url() = "http://localhost:$port/v1/reservations/search"
}
