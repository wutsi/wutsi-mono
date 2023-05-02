package com.wutsi.blog

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthControllerTest {
    @Autowired
    lateinit var rest: TestRestTemplate

    @Test
    fun get() {
        val result = rest.getForEntity("/actuator/health", Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
    }
}
