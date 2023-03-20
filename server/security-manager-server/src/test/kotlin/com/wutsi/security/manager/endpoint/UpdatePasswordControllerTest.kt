package com.wutsi.security.manager.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.security.manager.dao.PasswordRepository
import com.wutsi.security.manager.dto.UpdatePasswordRequest
import com.wutsi.security.manager.error.ErrorURN
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UpdatePasswordController.sql"])
class UpdatePasswordControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var dao: PasswordRepository

    @Test
    fun update() {
        // WHEN
        val request = UpdatePasswordRequest(
            value = "123",
        )
        rest.put(url(), request)

        // THEN

        val password = dao.findById(100).get()
        assertEquals(32, password.value.length)
    }

    @Test
    fun notFound() {
        // WHEN
        val request = UpdatePasswordRequest(
            value = "123",
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest = createRestTemplate(999999)
            rest.put(url(), request)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PASSWORD_NOT_FOUND.urn, response.error.code)
    }

    @Test
    fun deleted() {
        // WHEN
        val request = UpdatePasswordRequest(
            value = "123",
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest = createRestTemplate(999)
            rest.put(url(), request)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PASSWORD_NOT_FOUND.urn, response.error.code)
    }

    private fun url() = "http://localhost:$port/v1/passwords"
}
