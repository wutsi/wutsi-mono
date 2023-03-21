package com.wutsi.marketplace.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.enums.ReservationStatus
import com.wutsi.marketplace.access.dao.ProductRepository
import com.wutsi.marketplace.access.dao.ReservationRepository
import com.wutsi.marketplace.access.dto.UpdateReservationStatusRequest
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UpdateReservationStatusController.sql"])
class UpdateReservationStatusControllerTest {
    @LocalServerPort
    val port: Int = 0

    val rest = RestTemplate()

    @Autowired
    private lateinit var dao: ReservationRepository

    @Autowired
    private lateinit var productDao: ProductRepository

    @Test
    fun cancel() {
        val request = UpdateReservationStatusRequest(
            status = ReservationStatus.CANCELLED.name,
        )
        val response = rest.postForEntity(url(100L), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val store = dao.findById(100).get()
        assertEquals(ReservationStatus.CANCELLED, store.status)
        assertNotNull(store.cancelled)

        assertEquals(11, productDao.findById(100L).get().quantity)
        assertNull(productDao.findById(101L).get().quantity)
        assertEquals(4, productDao.findById(102L).get().quantity)
    }

    @Test
    fun active() {
        val request = UpdateReservationStatusRequest(
            status = ReservationStatus.ACTIVE.name,
        )
        val response = rest.postForEntity(url(101L), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val store = dao.findById(101).get()
        assertEquals(ReservationStatus.ACTIVE, store.status)
    }

    @Test
    fun sameStatus() {
        val now = Date()

        Thread.sleep(2000)
        val request = UpdateReservationStatusRequest(
            status = ReservationStatus.ACTIVE.name,
        )
        val response = rest.postForEntity(url(102), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val store = dao.findById(102).get()
        assertTrue(store.updated.before(now))
    }

    @Test
    fun badStatus() {
        val request = UpdateReservationStatusRequest(
            status = ReservationStatus.UNKNOWN.name,
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(101), request, Any::class.java)
        }

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.STATUS_NOT_VALID.urn, response.error.code)
    }

    @Test
    fun notFound() {
        val request = UpdateReservationStatusRequest(
            status = ReservationStatus.ACTIVE.name,
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(9999), request, Any::class.java)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.RESERVATION_NOT_FOUND.urn, response.error.code)
    }

    private fun url(id: Long) =
        "http://localhost:$port/v1/reservations/$id/status"
}
