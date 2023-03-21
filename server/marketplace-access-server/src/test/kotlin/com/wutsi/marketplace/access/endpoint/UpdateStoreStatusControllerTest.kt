package com.wutsi.marketplace.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.enums.StoreStatus
import com.wutsi.marketplace.access.dao.StoreRepository
import com.wutsi.marketplace.access.dto.UpdateProductStatusRequest
import com.wutsi.marketplace.access.dto.UpdateStoreStatusRequest
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
@Sql(value = ["/db/clean.sql", "/db/UpdateStoreStatusController.sql"])
class UpdateStoreStatusControllerTest {
    @LocalServerPort
    val port: Int = 0

    val rest = RestTemplate()

    @Autowired
    private lateinit var dao: StoreRepository

    @Test
    fun suspend() {
        val request = UpdateStoreStatusRequest(
            status = StoreStatus.INACTIVE.name,
        )
        val response = rest.postForEntity(url(100L), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val store = dao.findById(100).get()
        assertEquals(StoreStatus.INACTIVE, store.status)
        assertNotNull(store.deactivated)
    }

    @Test
    fun review() {
        val request = UpdateStoreStatusRequest(
            status = StoreStatus.UNDER_REVIEW.name,
        )
        val response = rest.postForEntity(url(101L), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val store = dao.findById(101).get()
        assertEquals(StoreStatus.UNDER_REVIEW, store.status)
        assertNull(store.deactivated)
    }

    @Test
    fun activate() {
        val request = UpdateStoreStatusRequest(
            status = StoreStatus.ACTIVE.name,
        )
        val response = rest.postForEntity(url(102L), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val store = dao.findById(102).get()
        assertEquals(StoreStatus.ACTIVE, store.status)
        assertNull(store.deactivated)
    }

    @Test
    fun sameStatus() {
        val now = Date()

        Thread.sleep(2000)
        val request = UpdateProductStatusRequest(
            status = StoreStatus.INACTIVE.name,
        )
        val response = rest.postForEntity(url(300), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val store = dao.findById(300).get()
        assertTrue(store.updated.before(now))
    }

    @Test
    fun badStatus() {
        val request = UpdateProductStatusRequest(
            status = StoreStatus.UNKNOWN.name,
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(100), request, Any::class.java)
        }

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.STATUS_NOT_VALID.urn, response.error.code)
    }

    private fun url(id: Long) =
        "http://localhost:$port/v1/stores/$id/status"
}
