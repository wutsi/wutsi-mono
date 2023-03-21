package com.wutsi.marketplace.access.endpoint

import com.wutsi.marketplace.access.dto.SearchPictureRequest
import com.wutsi.marketplace.access.dto.SearchPictureResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchPictureController.sql"])
class SearchPictureControllerTest {
    @LocalServerPort
    val port: Int = 0

    private val rest = RestTemplate()

    @Test
    fun byProduct() {
        val request = SearchPictureRequest(
            productIds = listOf(100L),
        )
        val response = rest.postForEntity(url(), request, SearchPictureResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val pictures = response.body!!.pictures
        assertEquals(2, pictures.size)
        assertEquals(101L, pictures[0].id)
        assertEquals("https://www.img.com/101.png", pictures[0].url)
        assertEquals(102L, pictures[1].id)
        assertEquals("https://www.img.com/102.png", pictures[1].url)
    }

    @Test
    fun byUrl() {
        val request = SearchPictureRequest(
            pictureUrls = listOf("https://www.img.com/200.png", "https://www.img.com/299.png"),
        )
        val response = rest.postForEntity(url(), request, SearchPictureResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val pictures = response.body!!.pictures
        assertEquals(1, pictures.size)
        assertEquals(200L, pictures[0].id)
        assertEquals("https://www.img.com/200.png", pictures[0].url)
    }

    @Test
    fun byIds() {
        val request = SearchPictureRequest(
            pictureIds = listOf(999L, 101L, 102L),
        )
        val response = rest.postForEntity(url(), request, SearchPictureResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val pictures = response.body!!.pictures
        assertEquals(2, pictures.size)
        assertEquals(101L, pictures[0].id)
        assertEquals("https://www.img.com/101.png", pictures[0].url)
        assertEquals(102L, pictures[1].id)
        assertEquals("https://www.img.com/102.png", pictures[1].url)
    }

    private fun url() = "http://localhost:$port/v1/pictures/search"
}
