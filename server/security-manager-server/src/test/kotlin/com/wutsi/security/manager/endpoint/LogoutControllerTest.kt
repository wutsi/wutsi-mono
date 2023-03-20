package com.wutsi.security.manager.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.core.security.TokenBlacklistService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/LogoutController.sql"])
class LogoutControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var tokenBlacklistService: TokenBlacklistService

    @Autowired
    private lateinit var dao: com.wutsi.security.manager.dao.LoginRepository

    @Test
    fun logout() {
        // WHEN
        rest.delete(url())

        // THEN
        assertNotNull(dao.findById(100).get().expired)
        assertNotNull(dao.findById(101).get().expired)
        assertNotNull(dao.findById(102).get().expired)
        verify(tokenBlacklistService, times(3)).add(any(), any())
    }

    private fun url() = "http://localhost:$port/v1/auth"
}
