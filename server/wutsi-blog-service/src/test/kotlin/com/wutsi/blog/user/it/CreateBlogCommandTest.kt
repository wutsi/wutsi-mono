package com.wutsi.blog.user.it

import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.blog.user.dto.CreateBlogCommand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
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
import java.util.Date
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/user/CreateBlogCommand.sql"])
internal class CreateBlogCommandTest : ClientHttpRequestInterceptor {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var userDao: UserRepository

    @Autowired
    private lateinit var subscriberDao: SubscriptionRepository

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
    fun create() {
        // GIVEN
        val request = CreateBlogCommand(1L)
        val now = Date()
        Thread.sleep(1000L)

        // WHEN
        val response = rest.postForEntity("/v1/users/commands/create-blog", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val user = userDao.findById(1L)
        assertTrue(user.get().blog)
        assertTrue(user.get().modificationDateTime.after(now))

        Thread.sleep(15000)
        val subscriber = subscriberDao.findByUserIdAndSubscriberId(10, user.get().id!!)
        assertNotNull(subscriber)
    }

    @Test
    fun alreadyBlog() {
        // GIVEN
        accessToken = "session-user-100"
        val request = CreateBlogCommand(100)
        val now = Date()
        Thread.sleep(1000L)

        // WHEN
        val response = rest.postForEntity("/v1/users/commands/create-blog", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val user = userDao.findById(100L)
        assertTrue(user.get().blog)
        assertFalse(user.get().modificationDateTime.after(now))
    }

    @Test
    fun error403() {
        // GIVEN
        val request = CreateBlogCommand(100L)

        // WHEN
        val response = rest.postForEntity("/v1/users/commands/create-blog", request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }
}
