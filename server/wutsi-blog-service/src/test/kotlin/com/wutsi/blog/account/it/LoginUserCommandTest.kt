package com.wutsi.blog.account.it

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.account.dao.SessionRepository
import com.wutsi.blog.account.dto.LoginUserCommand
import com.wutsi.blog.account.dto.LoginUserResponse
import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.tracing.TracingContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/account/LoginUserCommand.sql"])
class LoginUserCommandTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var sessionDao: SessionRepository

    @Autowired
    private lateinit var userDao: UserRepository

    @Autowired
    private lateinit var eventStore: EventStore

    @MockBean
    protected lateinit var tracingContext: TracingContext

    private val deviceId: String = "device-id"

    @BeforeEach
    fun setUp() {
        doReturn(deviceId).whenever(tracingContext).deviceId()
        doReturn(UUID.randomUUID().toString()).whenever(tracingContext).traceId()
        doReturn("test").whenever(tracingContext).clientId()
    }

    @Test
    fun signup() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val request = LoginUserCommand(
            accessToken = "signup",
            refreshToken = "signup-refresh",
            email = "john.smith123@gmail.com",
            pictureUrl = "https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg",
            fullName = "John Smith",
            provider = "facebook",
            providerUserId = "john.smith",
            language = "en",
            country = "CM",
            ip = "10.2.1.1",
            storyId = 111L,
            referer = "story",
        )
        val result = rest.postForEntity("/v1/auth/commands/login", request, LoginUserResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val token = result.body!!.accessToken
        assertEquals(request.accessToken, token)

        val session = sessionDao.findByAccessToken(token).get()
        assertEquals(request.accessToken, session.accessToken)
        assertEquals(request.refreshToken, session.refreshToken)
        assertNotNull(session.loginDateTime)
        assertNull(session.logoutDateTime)
        assertNull(session.runAsUser)
        assertNotNull(session.accessToken)
        assertEquals(request.ip, session.ip)
        assertEquals(request.storyId, session.storyId)
        assertEquals(request.referer, session.referer)
        assertEquals(deviceId, session.deviceId)

        val events = eventStore.events(
            streamId = StreamId.AUTHENTICATION,
            entityId = token,
            userId = session.account.user.id?.toString(),
            type = EventType.USER_LOGGED_IN_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(30000)
        val user = userDao.findByEmailIgnoreCase(request.email!!).get()
        assertEquals("john.smith123", user.name)
        assertTrue(user.creationDateTime.after(now))
        assertEquals(request.email, user.email)
        assertEquals(request.fullName, user.fullName)
        assertNotNull(request.pictureUrl)
        assertNotNull(user.pictureUrl)
        assertNotNull(user.lastLoginDateTime)
        assertTrue(user.lastLoginDateTime!!.after(now))
        assertEquals(request.language, user.language)
        assertEquals(request.country?.lowercase(), user.country)
    }

    @Test
    fun loginByEmail() {
        // GIVEN
        doReturn("NONE").whenever(tracingContext).deviceId()

        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val request = LoginUserCommand(
            accessToken = "login",
            refreshToken = "login-refresh",
            email = "login@gmail.com",
            pictureUrl = "https://www.foo.com/pic.png",
            fullName = "John Smith",
            provider = "facebook",
            providerUserId = "jane.doe",
            language = "es",
            country = "IN",
            ip = "10.2.1.1",
            storyId = 111L,
            referer = "story",
        )
        val result = rest.postForEntity("/v1/auth/commands/login", request, LoginUserResponse::class.java)

        // THEN
        assertEquals(result.statusCode, HttpStatus.OK)

        val token = result.body!!.accessToken
        assertEquals(request.accessToken, token)

        val session = sessionDao.findByAccessToken(token).get()
        assertEquals(request.accessToken, session.accessToken)
        assertEquals(request.refreshToken, session.refreshToken)
        assertNotNull(session.loginDateTime)
        assertNull(session.logoutDateTime)
        assertNotNull(session.accessToken)
        assertNull(session.runAsUser)
        assertEquals(20L, session.account.id)
        assertEquals(request.ip, session.ip)
        assertEquals(request.storyId, session.storyId)
        assertEquals(request.referer, session.referer)
        assertNull(session.deviceId)

        val events = eventStore.events(
            streamId = StreamId.AUTHENTICATION,
            entityId = token,
            userId = session.account.user.id?.toString(),
            type = EventType.USER_LOGGED_IN_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(15000)
        val user = userDao.findByEmailIgnoreCase(request.email!!).get()
        assertFalse(user.creationDateTime.after(now))
        assertEquals("login@gmail.com", user.email)
        assertEquals("Jane Doe", user.fullName)
        assertEquals("http://localhost:0/storage/image/upload/v1312461204/sample.jpg", user.pictureUrl)
        assertNotNull(user.lastLoginDateTime)
        assertTrue(user.lastLoginDateTime!!.after(now))
        assertEquals("fr", user.language)
        assertNull(user.country)
    }

    @Test
    fun loginByProviderId() {
        // GIVEN
        doReturn("").whenever(tracingContext).deviceId()

        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val request = LoginUserCommand(
            accessToken = "login",
            refreshToken = "login-refresh",
            pictureUrl = "https://www.foo.com/pic.png",
            fullName = "John Smith",
            provider = "facebook",
            providerUserId = "jane.doe",
            language = "es",
        )
        val result = rest.postForEntity("/v1/auth/commands/login", request, LoginUserResponse::class.java)

        // THEN
        assertEquals(result.statusCode, HttpStatus.OK)

        val token = result.body!!.accessToken
        assertEquals(request.accessToken, token)

        val session = sessionDao.findByAccessToken(token).get()
        assertEquals(request.accessToken, session.accessToken)
        assertEquals(request.refreshToken, session.refreshToken)
        assertNotNull(session.loginDateTime)
        assertNull(session.logoutDateTime)
        assertNotNull(session.accessToken)
        assertNull(session.runAsUser)
        assertEquals(20L, session.account.id)
        assertNull(session.deviceId)

        val events = eventStore.events(
            streamId = StreamId.AUTHENTICATION,
            entityId = token,
            userId = session.account.user.id?.toString(),
            type = EventType.USER_LOGGED_IN_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(15000)
        val user = userDao.findById(session.account.user.id!!).get()
        assertFalse(user.creationDateTime.after(now))
        assertEquals("login@gmail.com", user.email)
        assertEquals("Jane Doe", user.fullName)
        assertEquals("http://localhost:0/storage/image/upload/v1312461204/sample.jpg", user.pictureUrl)
        assertNotNull(user.lastLoginDateTime)
        assertTrue(user.lastLoginDateTime!!.after(now))
        assertEquals("fr", user.language)
    }

    @Test
    fun loginSuspended() {
        // WHEN
        val request = LoginUserCommand(
            accessToken = UUID.randomUUID().toString(),
            email = "suspended@gmail.com",
            pictureUrl = "https://www.foo.com/pic.png",
            fullName = "John Smith",
            provider = "facebook",
            providerUserId = "suspended",
        )
        val result = rest.postForEntity("/v1/auth/commands/login", request, ErrorResponse::class.java)

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.USER_SUSPENDED, result.body!!.error.code)
    }
}
