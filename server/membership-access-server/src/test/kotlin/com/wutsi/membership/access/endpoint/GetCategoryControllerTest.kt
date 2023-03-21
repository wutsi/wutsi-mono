package com.wutsi.membership.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.membership.access.dto.GetCategoryResponse
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
@Sql(value = ["/db/clean.sql", "/db/GetCategoryController.sql"])
class GetCategoryControllerTest : AbstractLanguageAwareControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun get() {
        // WHEN
        val response = rest.getForEntity(url(1000), GetCategoryResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val category = response.body!!.category
        assertEquals(1000L, category.id)
        assertEquals("Advertising/Marketing", category.title)
    }

    @Test
    fun french() {
        // GIVEN
        language = "fr"

        // WHEN
        val response = rest.getForEntity(url(1000), GetCategoryResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val category = response.body!!.category
        assertEquals(1000L, category.id)
        assertEquals("Marketing/Publicité", category.title)
    }

    @Test
    fun notFound() {
        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url(9999), GetCategoryResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.CATEGORY_NOT_FOUND.urn, response.error.code)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/categories/$id"
}
