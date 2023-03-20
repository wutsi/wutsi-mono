package com.wutsi.security.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.security.manager.dto.GetKeyResponse
import com.wutsi.security.manager.error.ErrorURN
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/GetKeyController.sql"])
public class GetKeyControllerTest {
    @LocalServerPort
    public val port: Int = 0

    protected val rest = RestTemplate()

    @Test
    public fun getKey() {
        // WHEN
        val response = rest.getForEntity(url(100), GetKeyResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val key = response.body!!.key
        assertEquals("RSA", key.algorithm)
        assertEquals("public-key-1", key.content)
    }

    @Test
    public fun expired() {
        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url(200), GetKeyResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.KEY_EXPIRED.urn, response.error.code)
    }

    @Test
    public fun notFound() {
        // WHEN
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url(9999), GetKeyResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.KEY_NOT_FOUND.urn, response.error.code)
    }

    private fun url(id: Long) = "http://localhost:$port/v1/keys/$id"
}
