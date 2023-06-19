package com.wutsi.blog.user.it

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.blog.user.dto.UpdateUserAttributeCommand
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/user/UpdateUserAttributeCommand.sql"])
internal class UpdateUserAttributeCommandTest : ClientHttpRequestInterceptor {
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
    fun rename() {
        val name = "new-name" + System.currentTimeMillis()
        testUpdateAttribute(1, "name", name)
    }

    @Test
    fun renameDuplicateName() {
        testUpdateAttributeWithError(1, "name", "duplicate.name", HttpStatus.CONFLICT, ErrorCode.USER_NAME_DUPLICATE)
    }

    @Test
    fun updateEmail() {
        val email = "new-email" + System.currentTimeMillis() + "@gmail.com"
        testUpdateAttribute(1, "email", email)
    }

    @Test
    fun updateDuplicateEmail() {
        testUpdateAttributeWithError(1,
            "email",
            "duplicate.email@gmail.com",
            HttpStatus.CONFLICT,
            ErrorCode.USER_EMAIL_DUPLICATE)
    }

    @Test
    fun updateFullname() {
        testUpdateAttribute(1, "full_name", "Joe Moll")
    }

    @Test
    fun updatePicture() {
        testUpdateAttribute(1, "picture_url", "http://www.google.com/picture/1.png")
    }

    @Test
    fun updateWebsiteUrl() {
        testUpdateAttribute(1, "website_url", "http://www.google.com/ray.sponsible")
    }

    @Test
    fun updateBiography() {
        testUpdateAttribute(1, "biography", "Yo man")
    }

    @Test
    fun updateFacebookId() {
        testUpdateAttribute(1, "facebook_id", "ray.sponsible")
    }

    @Test
    fun updateTwitterId() {
        testUpdateAttribute(1, "twitter_id", "ray.sponsible")
    }

    @Test
    fun updateLanguage() {
        testUpdateAttribute(1, "language", "fr")
    }

    @Test
    fun testTelegram() {
        testUpdateAttribute(1, "telegram_id", "foo.bar")
    }

    @Test
    fun testWhatsapp() {
        testUpdateAttribute(1, "whatsapp_id", "foo.bar")
    }

    @Test
    fun updateInvalidAttribute() {
        testUpdateAttributeWithError(
            1,
            "unknwon-attribute",
            "duplicate.email@gmail.com",
            HttpStatus.CONFLICT,
            "invalid_attribute",
        )
    }

    @Test
    fun error403() {
        val name = "new-name" + System.currentTimeMillis()
        val request = UpdateUserAttributeCommand(
            userId = 2L,
            name = "name",
            value = name,
        )

        val result = rest.postForEntity("/v1/users/commands/update-attribute", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }

    private fun testUpdateAttribute(userId: Long, name: String, value: String?) {
        val result = updateAttribute(userId, name, value)

        assertEquals(HttpStatus.OK, result.statusCode)

        val user = userDao.findById(userId).get()
        if ("language" == name) {
            assertEquals(value, user.language)
        } else if ("twitter_id" == name) {
            assertEquals(value, user.twitterId)
        } else if ("facebook_id" == name) {
            assertEquals(value, user.facebookId)
        } else if ("biography" == name) {
            assertEquals(value, user.biography)
        } else if ("website_url" == name) {
            assertEquals(value, user.websiteUrl)
        } else if ("picture_url" == name) {
            assertEquals(value, user.pictureUrl)
        } else if ("full_name" == name) {
            assertEquals(value, user.fullName)
        } else if ("email" == name) {
            assertEquals(value, user.email)
        } else if ("name" == name) {
            assertEquals(value, user.name)
        }
    }

    private fun testUpdateAttributeWithError(
        userId: Long,
        name: String,
        value: String?,
        status: HttpStatus,
        errorCode: String,
    ) {
        val result = updateAttribute(userId, name, value, ErrorResponse::class.java)

        assertEquals(status, result.statusCode)
        assertEquals(errorCode, (result.body as ErrorResponse?)?.error?.code)
    }

    private fun updateAttribute(
        userId: Long,
        name: String,
        value: String?,
        responseType: Class<*> = Any::class.java,
    ): ResponseEntity<*> {
        val request = UpdateUserAttributeCommand(
            userId = userId,
            name = name,
            value = value,
        )

        return rest.postForEntity("/v1/users/commands/update-attribute", request, responseType)
    }
}
