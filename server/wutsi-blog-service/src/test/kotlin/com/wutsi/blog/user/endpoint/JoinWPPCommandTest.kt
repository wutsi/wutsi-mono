package com.wutsi.blog.user.endpoint

import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.blog.user.dto.JoinWPPCommand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/user/JoinWPPCommand.sql"])
internal class JoinWPPCommandTest : ClientHttpRequestInterceptor {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var userDao: UserRepository

    private var accessToken: String? = "session-ray"

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        accessToken?.let {
            request.headers.setBearerAuth(it)
        }
        return execution.execute(request, body)
    }

    @BeforeEach
    fun setUp() {
        rest.restTemplate.interceptors = listOf(this)
    }

    @Test
    fun join() {
        // GIVEN
        val request = JoinWPPCommand(1L)

        // WHEN
        val response = rest.postForEntity("/v1/users/commands/join-wpp", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val user = userDao.findById(1L)
        assertTrue(user.get().wpp)
    }

    @Test
    fun error403() {
        // GIVEN
        val request = JoinWPPCommand(100L)

        // WHEN
        val response = rest.postForEntity("/v1/users/commands/join-wpp", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }
}
