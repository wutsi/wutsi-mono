package com.wutsi.blog.user.it

import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.platform.core.error.ErrorResponse
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
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/user/GetUserQuery.sql"])
class GetUserQueryTest : ClientHttpRequestInterceptor {
    @Autowired
    private lateinit var rest: TestRestTemplate

    private var accessToken: String? = null

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
        accessToken = null
        rest.restTemplate.interceptors = listOf(this)
    }

    @Test
    fun get() {
        accessToken = "session-10"
        val result = rest.getForEntity("/v1/users/1", GetUserResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val user = result.body!!.user
        assertEquals(1L, user.id)
        assertEquals("ray.sponsible", user.name)
        assertEquals("Angel investor", user.biography)
        assertEquals("Ray Sponsible", user.fullName)
        assertEquals("https://picture.com/ray.sponsible", user.pictureUrl)
        assertEquals("ray.sponsible@gmail.com", user.email)
        assertEquals("https://me.com/ray.sponsible", user.websiteUrl)
        assertTrue(user.superUser)
        assertTrue(user.blog)
        assertEquals("ray.sponsible", user.telegramId)
        assertEquals("23799505555", user.whatsappId)
        assertEquals("23799505555", user.whatsappId)
        assertEquals(10L, user.pinStoryId)
        assertEquals(1L, user.draftStoryCount)
        assertEquals(2L, user.publishStoryCount)
        assertEquals(33, user.subscriberCount)
        assertTrue(user.subscribed)
    }

    @Test
    fun getNotFound() {
        val result = rest.getForEntity("/v1/users/999", ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals("user_not_found", result.body!!.error.code)
    }

    @Test
    fun getByName() {
        val result = rest.getForEntity("/v1/users/@/jane.doe", GetUserResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val user = result.body!!.user
        assertEquals(2L, user.id)
        assertEquals("jane.doe", user.name)
        assertNull(user.biography)
        assertEquals("Jane Doe", user.fullName)
        assertEquals("https://picture.com/jane.doe", user.pictureUrl)
        assertEquals("jane.doe@gmail.com", user.email)
        assertEquals("https://me.com/jane.doe", user.websiteUrl)
        assertNull(user.pinStoryId)
        assertEquals(0L, user.draftStoryCount)
        assertEquals(0L, user.publishStoryCount)
        assertEquals(0L, user.subscriberCount)
        assertFalse(user.subscribed)
    }

    @Test
    fun getByNameNotFound() {
        val result = rest.getForEntity("/v1/users/@/xxxx", ErrorResponse::class.java)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals("user_not_found", result.body!!.error.code)
    }
}
