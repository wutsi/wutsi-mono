package com.wutsi.marketplace.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.enums.FundraisingStatus
import com.wutsi.marketplace.access.dao.FundraisingRepository
import com.wutsi.marketplace.access.dto.UpdateFundraisingStatusRequest
import com.wutsi.marketplace.access.dto.UpdateProductStatusRequest
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
@Sql(value = ["/db/clean.sql", "/db/UpdateFundraisingStatusController.sql"])
public class UpdateFundraisingStatusControllerTest {
    @LocalServerPort
    val port: Int = 0

    val rest = RestTemplate()

    @Autowired
    private lateinit var dao: FundraisingRepository

    @Test
    fun suspend() {
        val request = UpdateFundraisingStatusRequest(
            status = FundraisingStatus.INACTIVE.name,
        )
        val response = rest.postForEntity(url(100L), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fundraising = dao.findById(100).get()
        assertEquals(FundraisingStatus.INACTIVE, fundraising.status)
        assertNotNull(fundraising.deactivated)
    }

    @Test
    fun review() {
        val request = UpdateFundraisingStatusRequest(
            status = FundraisingStatus.UNDER_REVIEW.name,
        )
        val response = rest.postForEntity(url(101L), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fundraising = dao.findById(101).get()
        assertEquals(FundraisingStatus.UNDER_REVIEW, fundraising.status)
        assertNull(fundraising.deactivated)
    }

    @Test
    fun activate() {
        val request = UpdateFundraisingStatusRequest(
            status = FundraisingStatus.ACTIVE.name,
        )
        val response = rest.postForEntity(url(102L), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fundraising = dao.findById(102).get()
        assertEquals(FundraisingStatus.ACTIVE, fundraising.status)
        assertNull(fundraising.deactivated)
    }

    @Test
    fun sameStatus() {
        val now = Date()

        Thread.sleep(2000)
        val request = UpdateProductStatusRequest(
            status = FundraisingStatus.INACTIVE.name,
        )
        val response = rest.postForEntity(url(300), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fundraising = dao.findById(300).get()
        assertTrue(fundraising.updated.before(now))
    }

    @Test
    fun badStatus() {
        val request = UpdateProductStatusRequest(
            status = FundraisingStatus.UNKNOWN.name,
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
        "http://localhost:$port/v1/fundraisings/$id/status"
}
