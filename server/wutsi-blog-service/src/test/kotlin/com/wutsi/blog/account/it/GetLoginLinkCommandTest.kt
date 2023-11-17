package com.wutsi.blog.account.it

import com.wutsi.blog.account.dto.CreateLoginLinkCommand
import com.wutsi.blog.account.dto.CreateLoginLinkResponse
import com.wutsi.blog.account.dto.GetLoginLinkResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/account/GetLoginLinkCommand.sql"])
class GetLoginLinkCommandTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun get() {
        // GIVEN
        val request = CreateLoginLinkCommand(
            referer = "foo",
            redirectUrl = "https://www.google.ca",
            storyId = 111L,
            email = "ray.sponsible@gmail.com",
            language = "en"
        )
        val response = rest.postForEntity("/v1/auth/links/create", request, CreateLoginLinkResponse::class.java)

        // WHEN
        val id = response.body.linkId
        val result = rest.getForEntity("/v1/auth/links/$id", GetLoginLinkResponse::class.java)

        // THEN
        assertEquals(result.statusCode, HttpStatus.OK)

        val payload = result.body!!.link
        assertEquals(request.referer, payload.referer)
        assertEquals(request.redirectUrl, payload.redirectUrl)
        assertEquals(request.storyId, payload.storyId)
        assertEquals(request.email, payload.email)
        assertEquals(request.language, payload.language)
    }

    @Test
    fun `not found`() {
        // WHEN
        val id = UUID.randomUUID().toString()
        val result = rest.getForEntity("/v1/auth/links/$id", GetLoginLinkResponse::class.java)

        // THEN
        assertEquals(result.statusCode, HttpStatus.NOT_FOUND)
    }

    @Test
    fun `invalid type`() {
        // WHEN
        val id = UUID.randomUUID().toString()
        val result = rest.getForEntity("/v1/auth/links/event-100", GetLoginLinkResponse::class.java)

        // THEN
        assertEquals(result.statusCode, HttpStatus.NOT_FOUND)
    }
}
