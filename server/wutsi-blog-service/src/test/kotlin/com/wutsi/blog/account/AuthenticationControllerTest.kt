package com.wutsi.blog.account

import com.wutsi.blog.EventHandler
import com.wutsi.blog.account.dao.SessionRepository
import com.wutsi.blog.account.dao.UserRepository
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.user.AuthenticateRequest
import com.wutsi.blog.client.user.AuthenticateResponse
import com.wutsi.blog.client.user.GetSessionResponse
import com.wutsi.blog.client.user.RunAsRequest
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.tracing.TracingContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.net.URL
import java.time.Clock
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/AuthenticationController.sql"])
class AuthenticationControllerTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var sessionDao: SessionRepository

    @Autowired
    private lateinit var userDao: UserRepository

    @Autowired
    private lateinit var clock: Clock

    @Autowired
    private lateinit var events: EventHandler

    @Autowired
    private lateinit var storage: StorageService

    @BeforeEach
    fun setUp() {
        events.init()
    }

    @Test
    fun signup() {
        val request = AuthenticateRequest(
            accessToken = "signup",
            refreshToken = "signup-refresh",
            email = "john.smith123@gmail.com",
            pictureUrl = "https://image.com/john.smith.json",
            fullName = "John Smith",
            provider = "facebook",
            providerUserId = "john.smith",
            language = "en",
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/auth", request, AuthenticateResponse::class.java)

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

        val user = userDao.findByEmailIgnoreCase(request.email!!).get()
        assertEquals("john.smith123", user.name)
        assertNotNull(user.creationDateTime)
        assertEquals(request.email, user.email)
        assertEquals(request.fullName, user.fullName)
        assertEquals(request.pictureUrl, user.pictureUrl)
        assertEquals(1L, user.loginCount)
        assertNotNull(user.lastLoginDateTime)
        assertEquals(request.language, user.language)
        assertEquals(request.siteId, user.siteId)
    }

    @Test
    fun `user's picture is downloaded on signup`() {
        val request = AuthenticateRequest(
            accessToken = "signup-download",
            refreshToken = "signup-refresh",
            email = "john.smith456@gmail.com",
            pictureUrl = "https://i.pinimg.com/564x/0e/d6/05/0ed6058d49b88102d0165a2905fa1176.jpg",
            fullName = "John Smith",
            provider = "facebook",
            providerUserId = "john.smith",
            language = "en",
            siteId = 11L,
        )
        rest.postForEntity("/v1/auth", request, AuthenticateResponse::class.java)

        Thread.sleep(5000)
        val user = userDao.findByEmailIgnoreCase(request.email!!).get()
        assertTrue(storage.contains(URL(user.pictureUrl)))
    }

    @Test
    fun `signup fire LoginEvent`() {
        val deviceUID = UUID.randomUUID().toString()
        val userAgent =
            "Mozilla/5.0 (Linux; Android 8.0.0;) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Mobile Safari/537.36"
        val request = AuthenticateRequest(
            accessToken = "signup-xxx",
            refreshToken = null,
            email = "john.smith123-xxx@gmail.com",
            pictureUrl = "https://image.com/john.smith.json",
            fullName = "John Smith",
            provider = "facebook",
            providerUserId = "john.smith.xxx",
            siteId = 11L,
        )
        val headers = HttpHeaders()
        headers.add(TracingContext.HEADER_DEVICE_ID, deviceUID)
        headers.add(HttpHeaders.USER_AGENT, userAgent)

        val entity = HttpEntity<Any>(request, headers)
        val result = rest.exchange("/v1/auth", HttpMethod.POST, entity, AuthenticateResponse::class.java)
        val response = result.body!!

        val event = events.loginEvent
        assertNotNull(event)
        assertEquals(response.userId, event.userId)
        assertEquals(deviceUID, event.deviceUID)
        assertEquals(userAgent, event.userAgent)
        assertEquals(1L, event.loginCount)
    }

    @Test
    fun login() {
        val request = AuthenticateRequest(
            accessToken = "login",
            refreshToken = "login-refresh",
            email = "login@gmail.com",
            pictureUrl = "https://www.google.com/images/jane.doe.png",
            fullName = "John Smith",
            provider = "facebook",
            providerUserId = "jane.doe",
            language = "es",
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/auth", request, AuthenticateResponse::class.java)

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
    }

    @Test
    fun `login fire LoginEvent`() {
        val deviceUID = UUID.randomUUID().toString()
        val userAgent =
            "Mozilla/5.0 (Linux; Android 8.0.0;) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Mobile Safari/537.36"
        val request = AuthenticateRequest(
            accessToken = "login",
            refreshToken = "login-refresh",
            email = "login@gmail.com",
            pictureUrl = "https://www.google.com/images/jane.doe.png",
            fullName = "John Smith",
            provider = "facebook",
            providerUserId = "jane.doe",
            siteId = 11L,
        )
        val headers = HttpHeaders()
        headers.add(TracingContext.HEADER_DEVICE_ID, deviceUID)
        headers.add(HttpHeaders.USER_AGENT, userAgent)

        val entity = HttpEntity<Any>(request, headers)
        val result = rest.exchange("/v1/auth", HttpMethod.POST, entity, AuthenticateResponse::class.java)
        val response = result.body!!

        val event = events.loginEvent
        assertNotNull(event)
        assertEquals(response.userId, event.userId)
        assertEquals(deviceUID, event.deviceUID)
        assertEquals(userAgent, event.userAgent)
        assertTrue(event.loginCount > 1)
    }

    @Test
    fun `login without email`() {
        val request = AuthenticateRequest(
            accessToken = "login-no-email",
            refreshToken = null,
            fullName = "Stan Smith",
            provider = "facebook",
            providerUserId = "login.without.email",
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/auth", request, AuthenticateResponse::class.java)

        assertEquals(result.statusCode, HttpStatus.OK)

        val token = result.body!!.accessToken
        assertEquals(request.accessToken, token)

        val session = sessionDao.findByAccessToken(token).get()
        assertEquals(request.accessToken, session.accessToken)
        assertEquals(request.refreshToken, session.refreshToken)
        assertNotNull(session.loginDateTime)
        assertNull(session.logoutDateTime)
        assertEquals(30L, session.account.id)
    }

    @Test
    fun `login from a channel`() {
        val request = AuthenticateRequest(
            accessToken = "twitter-token",
            refreshToken = "twitter-token-refresh",
            email = null,
            pictureUrl = "https://image.com/john.smith.json",
            fullName = "Twitter User",
            provider = ChannelType.twitter.name,
            providerUserId = "7777",
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/auth", request, AuthenticateResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        assertEquals(7L, result.body!!.userId)

        val session = sessionDao.findByAccessToken(request.accessToken!!).get()
        assertEquals(request.accessToken, session.accessToken)
        assertEquals(request.accessToken, session.accessToken)
        assertEquals(request.refreshToken, session.refreshToken)
        assertNotNull(session.loginDateTime)
        assertNull(session.logoutDateTime)
    }

    @Test
    fun `user's picture downloaded on login`() {
        val request = AuthenticateRequest(
            accessToken = "update-account",
            refreshToken = "update-account-refresh",
            email = "john.smith456@gmail.com",
            pictureUrl = "https://i.pinimg.com/564x/0e/d6/05/0ed6058d49b88102d0165a2905fa1176.jpg",
            fullName = "Update Account",
            provider = "facebook",
            providerUserId = "fb_update_account",
            siteId = 11L,
        )
        rest.postForEntity("/v1/auth", request, AuthenticateResponse::class.java)

        Thread.sleep(5000)
        val user = userDao.findByEmailIgnoreCase(request.email!!).get()
        assertTrue(storage.contains(URL(user.pictureUrl)))
    }

    @Test
    fun `user's profile is updated on login`() {
        val now = clock.millis()
        Thread.sleep(1000)

        val request = AuthenticateRequest(
            accessToken = "update-user",
            refreshToken = "update-user-refresh",
            email = "loginShouldUpdateAccount@gmail.com",
            fullName = "Update Account",
            pictureUrl = "https://image.com/john.smith.json",
            provider = "facebook",
            providerUserId = "fb_update_user",
            language = "es",
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/auth", request, AuthenticateResponse::class.java)

        assertEquals(result.statusCode, HttpStatus.OK)

        val user = userDao.findById(6L).get()
        assertEquals(request.email, user.email)
        assertEquals(request.fullName, user.fullName)
        assertEquals(request.pictureUrl, user.pictureUrl)
        assertEquals(14L, user.loginCount)
        assertTrue(user.lastLoginDateTime != null && user.lastLoginDateTime!!.time > now)
        assertEquals(request.language, user.language)
    }

    @Test
    fun `login with invid provider`() {
        val request = AuthenticateRequest(
            accessToken = "invalid-provider",
            refreshToken = "invalid-provider-refresh",
            email = "john.smith@gmail.com",
            pictureUrl = "https://image.com/john.smith.json",
            fullName = "John Smith",
            provider = "invalid-provider",
            providerUserId = "john.smith",
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/auth", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals("invalid_provider", result.body!!.error.code)
    }

    @Test
    fun logout() {
        rest.delete("/v1/auth/logout")

        val session = sessionDao.findByAccessToken("logout").get()
        assertNotNull(session.logoutDateTime)
    }

    @Test
    fun `logout invalid account`() {
        rest.delete("/v1/auth/not-found")
    }

    @Test
    fun `request session`() {
        val result = rest.getForEntity("/v1/auth/827c7013-f7ce-4238-947c-26fba6378d2d", GetSessionResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val session = result.body!!.session
        assertNotNull(session.loginDateTime)
        assertNull(session.logoutDateTime)
        assertEquals("827c7013-f7ce-4238-947c-26fba6378d2d", session.accessToken)
        assertEquals("827c7013-f7ce-4238-947c-26fba6378dff", session.refreshToken)
        assertEquals(1L, session.userId)
        assertEquals(10L, session.accountId)
    }

    @Test
    fun `request invalid session returns 404`() {
        val result = rest.getForEntity("/v1/auth/not-found", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        val error = result.body!!.error
        assertEquals("session_not_found", error.code)
    }

    @Test
    fun `request logged out session returns 404`() {
        val result = rest.getForEntity("/v1/auth/logout-expired", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        val error = result.body!!.error
        assertEquals("session_expired", error.code)
    }

    @Test
    fun `run-as user`() {
        val request = RunAsRequest(
            accessToken = "827c7013-f7ce-4238-947c-26fba6378d2d",
            userName = "login",
        )
        val result = rest.postForEntity("/v1/auth/as", request, AuthenticateResponse::class.java)

        assertEquals(result.statusCode, HttpStatus.OK)

        val token = result.body!!.accessToken
        assertEquals(request.accessToken, token)

        val session = sessionDao.findByAccessToken(token).get()
        assertEquals(2L, session.runAsUser?.id)
    }

    @Test
    fun `run-as with non-super-user`() {
        val request = RunAsRequest(
            accessToken = "827c7013-f7ce-4238-947c-26fba6378d2f",
            userName = "login",
        )
        val result = rest.postForEntity("/v1/auth/as", request, ErrorResponse::class.java)

        assertEquals(result.statusCode, HttpStatus.CONFLICT)
        assertEquals("permission_denied", result.body!!.error.code)
    }

    @Test
    fun `run as invalid user`() {
        val request = RunAsRequest(
            accessToken = "827c7013-f7ce-4238-947c-26fba6378d2d",
            userName = "?????",
        )
        val result = rest.postForEntity("/v1/auth/as", request, ErrorResponse::class.java)

        assertEquals(result.statusCode, HttpStatus.NOT_FOUND)
        assertEquals("user_not_found", result.body!!.error.code)
    }
}
