package com.wutsi.membership.access.endpoint

import com.wutsi.enums.PlaceType
import com.wutsi.membership.access.dto.SearchPlaceRequest
import com.wutsi.membership.access.dto.SearchPlaceResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchPlaceController.sql"])
class SearchPlaceControllerTest : AbstractLanguageAwareControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun byCountry() {
        // WHEN
        val request = SearchPlaceRequest(
            country = "CM",
        )
        val response = rest.postForEntity(url(), request, SearchPlaceResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val places = response.body!!.places
        assertEquals(7, places.size)
    }

    @Test
    fun cities() {
        // WHEN
        val request = SearchPlaceRequest(
            type = PlaceType.CITY.name,
            country = "CM",
        )
        val response = rest.postForEntity(url(), request, SearchPlaceResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val places = response.body!!.places
        assertEquals(5, places.size)

        val ids = places.map { it.id }
        assertEquals(listOf(101L, 104L, 103L, 102L, 100L), ids)
    }

    @Test
    fun keyword() {
        // GIVEN
        language = "fr"

        // WHEN
        val request = SearchPlaceRequest(
            country = "CM",
            keyword = "li",
        )
        val response = rest.postForEntity(url(), request, SearchPlaceResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val places = response.body!!.places
        assertEquals(2, places.size)

        val ids = places.map { it.id }
        assertEquals(listOf(104L, 103L), ids)
    }

    private fun url() = "http://localhost:$port/v1/places/search"
}
