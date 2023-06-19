package com.wutsi.blog.account.it

import com.wutsi.blog.account.dto.GetSessionResponse
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/account/GetSessionQuery.sql"])
class GetSessionQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun `request session`() {
        val result =
            rest.getForEntity("/v1/auth/sessions/827c7013-f7ce-4238-947c-26fba6378d2d", GetSessionResponse::class.java)

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
        val result = rest.getForEntity("/v1/auth/sessions/not-found", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        val error = result.body!!.error
        assertEquals("session_not_found", error.code)
    }

    @Test
    fun `request logged out session returns 404`() {
        val result = rest.getForEntity("/v1/auth/sessions/logout-expired", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        val error = result.body!!.error
        assertEquals("session_expired", error.code)
    }
}
