package com.wutsi.blog.account.it

import com.wutsi.blog.account.dao.SessionRepository
import com.wutsi.blog.account.dto.LogoutUserCommand
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.event.store.EventStore
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/account/LogoutUserCommand.sql"])
class LogoutUserCommandTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var sessionDao: SessionRepository

    @Autowired
    private lateinit var eventStore: EventStore

    @Test
    fun logout() {
        val now = Date()
        Thread.sleep(1000)

        val token = "827c7013-f7ce-4238-947c-26fba6378d2d"
        val request = LogoutUserCommand(token)
        val response = rest.postForEntity("/v1/auth/commands/logout", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val session = sessionDao.findByAccessToken(token).get()
        assertNotNull(session.logoutDateTime)
        assertTrue(session.logoutDateTime!!.after(now))

        val events = eventStore.events(
            streamId = StreamId.AUTHENTICATION,
            entityId = token,
            userId = "1",
            type = EventType.USER_LOGGED_OUT_EVENT
        )
        assertTrue(events.isNotEmpty())
    }

    @Test
    fun `invalid account`() {
        val request = LogoutUserCommand("?????")
        val response = rest.postForEntity("/v1/auth/commands/logout", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `already logged out`() {
        val now = Date()
        Thread.sleep(1000)

        val token = "827c7013-f7ce-4238-947c-26fba6378d2f"
        val request = LogoutUserCommand(token)
        val response = rest.postForEntity("/v1/auth/commands/logout", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val session = sessionDao.findByAccessToken(token).get()
        assertFalse(session.logoutDateTime!!.after(now))

        val events = eventStore.events(
            streamId = StreamId.AUTHENTICATION,
            entityId = token,
            userId = "4",
            type = EventType.USER_LOGGED_OUT_EVENT
        )
        assertFalse(events.isNotEmpty())
    }
}
