package com.wutsi.membership.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.membership.access.dto.GetPlaceResponse
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/GetPlaceController.sql"])
public class GetPlaceControllerTest : AbstractLanguageAwareControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun get() {
        // WHEN
        val response = rest.getForEntity(url(100), GetPlaceResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val place = response.body!!.place
        assertEquals(100L, place.id)
        assertEquals("Yaoundé", place.name)
        assertEquals("Yaoundé, Cameroon", place.longName)
        assertEquals("Africa/Douala", place.timezoneId)
        assertEquals("CM", place.country)
        assertEquals(1.1, place.longitude)
        assertEquals(2.2, place.latitude)
    }

    @Test
    fun french() {
        // GIVEN
        language = "fr"

        // WHEN
        val response = rest.getForEntity(url(100), GetPlaceResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val place = response.body!!.place
        assertEquals(100L, place.id)
        assertEquals("Yaoundé", place.name)
        assertEquals("Yaoundé, Cameroun", place.longName)
        assertEquals("Africa/Douala", place.timezoneId)
        assertEquals("CM", place.country)
        assertEquals(1.1, place.longitude)
        assertEquals(2.2, place.latitude)
    }

    @Test
    fun notFound() {
        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url(9999), GetPlaceResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PLACE_NOT_FOUND.urn, response.error.code)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/places/$id"
}
