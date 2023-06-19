package com.wutsi.blog.account.it

import com.wutsi.blog.account.dao.SessionRepository
import com.wutsi.blog.account.dto.LoginUserAsCommand
import com.wutsi.blog.account.dto.LoginUserResponse
import com.wutsi.blog.account.dto.UserLoggedInAsEventPayload
import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/account/LoginUserAsCommand.sql"])
class LoginUserAsCommandTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var sessionDao: SessionRepository

    @Autowired
    private lateinit var eventStore: EventStore

    @Test
    fun `run-as user`() {
        val request = LoginUserAsCommand(
            accessToken = "827c7013-f7ce-4238-947c-26fba6378d2d",
            userName = "login",
        )
        val result = rest.postForEntity("/v1/auth/commands/login-as", request, LoginUserResponse::class.java)

        assertEquals(result.statusCode, HttpStatus.OK)

        val token = result.body!!.accessToken
        assertEquals(request.accessToken, token)

        val session = sessionDao.findByAccessToken(token).get()
        assertEquals(2L, session.runAsUser?.id)

        val events = eventStore.events(
            streamId = StreamId.AUTHENTICATION,
            entityId = token,
            userId = "1",
            type = EventType.USER_LOGGED_IN_AS_EVENT,
        )
        assertTrue(events.isNotEmpty())
        val payload = events[0].payload as UserLoggedInAsEventPayload
        assertEquals(2L, payload.userId)
    }

    @Test
    fun `run-as with non-super-user`() {
        val request = LoginUserAsCommand(
            accessToken = "827c7013-f7ce-4238-947c-26fba6378d2f",
            userName = "login",
        )
        val result = rest.postForEntity("/v1/auth/commands/login-as", request, ErrorResponse::class.java)

        assertEquals(result.statusCode, HttpStatus.CONFLICT)
        assertEquals(ErrorCode.PERMISSION_DENIED, result.body!!.error.code)
    }

    @Test
    fun `run as invalid user`() {
        val request = LoginUserAsCommand(
            accessToken = "827c7013-f7ce-4238-947c-26fba6378d2d",
            userName = "?????",
        )
        val result = rest.postForEntity("/v1/auth/commands/login-as", request, ErrorResponse::class.java)

        assertEquals(result.statusCode, HttpStatus.NOT_FOUND)
        assertEquals(ErrorCode.USER_NOT_FOUND, result.body!!.error.code)
    }
}
