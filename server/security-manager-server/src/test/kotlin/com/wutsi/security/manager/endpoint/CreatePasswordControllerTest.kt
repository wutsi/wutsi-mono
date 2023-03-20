package com.wutsi.security.manager.endpoint

import com.wutsi.security.manager.dao.PasswordRepository
import com.wutsi.security.manager.dto.CreatePasswordRequest
import com.wutsi.security.manager.dto.CreatePasswordResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreatePasswordControllerTest {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: PasswordRepository

    protected val rest = RestTemplate()

    @Test
    fun create() {
        // WHEN
        val request = CreatePasswordRequest(
            value = "123",
            accountId = 1,
            username = "+15147580000",
        )
        val response = rest.postForEntity(url(), request, CreatePasswordResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val passwordId = response.body?.passwordId
        assertNotNull(passwordId)

        val password = dao.findById(passwordId).get()
        assertEquals(32, password.value.length)
        assertEquals(36, password.salt.length)
        assertEquals(request.accountId, password.accountId)
        assertEquals(request.username, password.username)
    }

    private fun url() = "http://localhost:$port/v1/passwords"
}
