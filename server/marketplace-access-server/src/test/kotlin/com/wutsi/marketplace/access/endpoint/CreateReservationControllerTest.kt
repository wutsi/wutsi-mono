package com.wutsi.marketplace.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.enums.ReservationStatus
import com.wutsi.marketplace.access.dao.ProductRepository
import com.wutsi.marketplace.access.dao.ReservationItemRepository
import com.wutsi.marketplace.access.dao.ReservationRepository
import com.wutsi.marketplace.access.dto.CreateReservationRequest
import com.wutsi.marketplace.access.dto.CreateReservationResponse
import com.wutsi.marketplace.access.dto.ReservationItem
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
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/CreateReservationController.sql"])
class CreateReservationControllerTest {
    @LocalServerPort
    val port: Int = 0

    private val rest = RestTemplate()

    @Autowired
    private lateinit var dao: ReservationRepository

    @Autowired
    private lateinit var itemDao: ReservationItemRepository

    @Autowired
    private lateinit var productDao: ProductRepository

    @Test
    fun reserve() {
        val request = CreateReservationRequest(
            orderId = "14309403",
            items = listOf(
                ReservationItem(productId = 100, quantity = 1),
                ReservationItem(productId = 102, quantity = 1),
                ReservationItem(productId = 103, quantity = 5),
            ),
        )
        val response = rest.postForEntity(url(), request, CreateReservationResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = response.body!!.reservationId
        val reservation = dao.findById(id)
        assertTrue(reservation.isPresent)
        assertEquals(request.orderId, reservation.get().orderId)
        assertEquals(ReservationStatus.ACTIVE, reservation.get().status)
        assertNull(reservation.get().cancelled)

        val items = itemDao.findByReservation(reservation.get())
        assertEquals(request.items.size, items.size)
        assertEquals(request.items[0].productId, items[0].product.id)
        assertEquals(request.items[0].quantity, items[0].quantity)
        assertEquals(request.items[1].productId, items[1].product.id)
        assertEquals(request.items[1].quantity, items[1].quantity)
        assertEquals(request.items[2].productId, items[2].product.id)
        assertEquals(request.items[2].quantity, items[2].quantity)

        assertEquals(9, productDao.findById(100).get().quantity)
        assertEquals(0, productDao.findById(102).get().quantity)
        assertEquals(5, productDao.findById(103).get().quantity)
    }

    @Test
    fun noLimit() {
        val request = CreateReservationRequest(
            orderId = "9999",
            items = listOf(
                ReservationItem(productId = 101, quantity = 10000),
            ),
        )
        val response = rest.postForEntity(url(), request, CreateReservationResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = response.body!!.reservationId
        val reservation = dao.findById(id)
        assertTrue(reservation.isPresent)
        assertEquals(request.orderId, reservation.get().orderId)
        assertNull(reservation.get().cancelled)

        val items = itemDao.findByReservation(reservation.get())
        assertEquals(request.items.size, items.size)
        assertEquals(request.items[0].productId, items[0].product.id)
        assertEquals(request.items[0].quantity, items[0].quantity)

        assertNull(productDao.findById(101).get().quantity)
    }

    @Test
    fun notAvailable() {
        val request = CreateReservationRequest(
            orderId = "14390493",
            items = listOf(
                ReservationItem(productId = 200, quantity = 5),
                ReservationItem(productId = 201, quantity = 5),
            ),
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(), request, Any::class.java)
        }

        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PRODUCT_NOT_AVAILABLE.urn, response.error.code)

        assertEquals(10, productDao.findById(200).get().quantity)
        assertEquals(1, productDao.findById(201).get().quantity)
    }

    @Test
    fun notFound() {
        val request = CreateReservationRequest(
            orderId = "21093209",
            items = listOf(
                ReservationItem(productId = 199, quantity = 1),
            ),
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(), request, Any::class.java)
        }

        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PRODUCT_NOT_AVAILABLE.urn, response.error.code)
    }

    private fun url() = "http://localhost:$port/v1/reservations"
}
