package com.wutsi.blog

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetupTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthControllerTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    private var greenMail = GreenMail(ServerSetupTest.SMTP)

    @BeforeEach
    fun setUp() {
        greenMail.start()
    }

    @AfterEach
    fun tearDown() {
        greenMail.setUser("username", "password")
        greenMail.start()
    }

    @Test
    fun get() {
        val result = rest.getForEntity("/actuator/health", Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
    }
}
