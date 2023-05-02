package com.wutsi.blog.pin

import com.wutsi.blog.client.pin.CreatePinRequest
import com.wutsi.blog.client.pin.CreatePinResponse
import com.wutsi.blog.client.pin.GetPinResponse
import com.wutsi.blog.pin.dao.PinRepository
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/PinController.sql"])
class PinControllerTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: PinRepository

    @Test
    fun `user pin a story`() {
        val request = CreatePinRequest(
            storyId = 10,
        )
        val response = rest.postForEntity("/v1/users/1/pin", request, CreatePinResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val pinId = response.body!!.pinId
        val pin = dao.findById(pinId)
        assertTrue(pin.isPresent)
        assertEquals(request.storyId, pin.get().storyId)
        assertEquals(1L, pin.get().userId)
    }

    @Test
    fun `user re-pin a story`() {
        val request = CreatePinRequest(
            storyId = 21,
        )
        val response = rest.postForEntity("/v1/users/2/pin", request, CreatePinResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val pinId = response.body!!.pinId
        val pin = dao.findById(pinId)
        assertTrue(pin.isPresent)
        assertEquals(request.storyId, pin.get().storyId)
        assertEquals(2L, pin.get().userId)
    }

    @Test
    fun `user cannot pin a story from another user`() {
        val request = CreatePinRequest(
            storyId = 20,
        )
        val response = rest.postForEntity("/v1/users/1/pin", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertEquals("not_owner", response.body!!.error.code)
    }

    @Test
    fun `user un-pin a story`() {
        assertTrue(dao.findById(3).isPresent)
        rest.delete("/v1/users/3/pin")

        val pin = dao.findById(3)
        assertFalse(pin.isPresent)
    }

    @Test
    fun `return pin`() {
        val response = rest.getForEntity("/v1/users/2/pin", GetPinResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val pin = response.body!!.pin
        assertEquals(2, pin.id)
        assertEquals(20, pin.storyId)
        assertEquals(2, pin.userId)
    }

    @Test
    fun `return 404 when no pin`() {
        val response = rest.getForEntity("/v1/users/4/pin", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("pin_not_found", response.body!!.error.code)
    }
}
