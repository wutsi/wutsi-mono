package com.wutsi.membership.access.endpoint

import com.wutsi.enums.PlaceType
import com.wutsi.membership.access.dao.PlaceRepository
import com.wutsi.membership.access.dto.SavePlaceRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SavePlaceController.sql"])
public class SavePlaceControllerTest {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: PlaceRepository

    val rest = RestTemplate()

    @Test
    fun create() {
        val request = SavePlaceRequest(
            id = 11111,
            name = "Domé",
            timezoneId = "Africa/Abidjan",
            country = "ci",
            longitude = 1111.0,
            latitude = 2222.0,
            type = PlaceType.COUNTRY.name,
        )
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val place = dao.findById(request.id).get()
        assertEquals(request.name, place.name)
        assertEquals("Dome", place.nameAscii)
        assertEquals(request.country.uppercase(), place.country)
        assertEquals(request.timezoneId, place.timezoneId)
        assertEquals(request.longitude, place.longitude)
        assertEquals(request.latitude, place.latitude)
        assertEquals(PlaceType.valueOf(request.type), place.type)
    }

    @Test
    fun update() {
        val request = SavePlaceRequest(
            id = 100,
            name = "Yagoué",
            timezoneId = "Africa/Abidjan",
            country = "CI",
            longitude = 1111.0,
            latitude = 2222.0,
            type = PlaceType.COUNTRY.name,
        )
        val response = rest.postForEntity(url(), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val place = dao.findById(request.id).get()
        assertEquals(request.name, place.name)
        assertEquals("Yagoue", place.nameAscii)
        assertEquals(request.country, place.country)
        assertEquals(request.timezoneId, place.timezoneId)
        assertEquals(request.longitude, place.longitude)
        assertEquals(request.latitude, place.latitude)
        assertEquals(PlaceType.valueOf(request.type), place.type)
    }

    private fun url() = "http://localhost:$port/v1/places"
}
