package com.wutsi.membership.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.membership.access.dao.PlaceRepository
import com.wutsi.membership.access.error.ErrorURN
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql"])
public class ImportPlaceControllerTest {
    @LocalServerPort
    public val port: Int = 0

    private val rest = RestTemplate()

    @Autowired
    private lateinit var dao: PlaceRepository

    @Test
    fun CM() =
        import("CM", 122)

    @Test
    fun CI() =
        import("CI", 66)

    @Test
    fun notSupported() {
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url("xx"), Any::class.java)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PLACE_FEED_NOT_FOUND.urn, response.error.code)
    }

    private fun import(country: String, count: Int) {
        val response = rest.getForEntity(url(country), Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val countries = dao.findByCountry(country)
        assertEquals(count, countries.size)
    }

    private fun url(country: String) = "http://localhost:$port/v1/places/import?country=$country"
}
