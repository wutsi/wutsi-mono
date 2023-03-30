package com.wutsi.marketplace.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.enums.FundraisingStatus
import com.wutsi.marketplace.access.dto.GetFundraisingResponse
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/GetFundraisingController.sql"])
public class GetFundraisingControllerTest {
    @LocalServerPort
    public val port: Int = 0

    private val rest = RestTemplate()

    @Test
    fun get() {
        val response = rest.getForEntity(url(100), GetFundraisingResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fundraising = response.body!!.fundraising
        assertEquals(100L, fundraising.id)
        assertEquals(100L, fundraising.accountId)
        assertEquals(333L, fundraising.businessId)
        assertEquals(FundraisingStatus.INACTIVE.name, fundraising.status)
        assertNotNull(fundraising.created)
        assertNotNull(fundraising.updated)
        assertNotNull(fundraising.deactivated)
    }

    @Test
    fun notFound() {
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url(99999), GetFundraisingResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.FUNDRAISING_NOT_FOUND.urn, response.error.code)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/fundraisings/$id"
}
