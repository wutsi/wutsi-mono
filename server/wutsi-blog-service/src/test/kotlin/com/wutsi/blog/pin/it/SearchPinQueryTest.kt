package com.wutsi.blog.pin.it

import com.wutsi.blog.pin.dto.SearchPinRequest
import com.wutsi.blog.pin.dto.SearchPinResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/pin/SearchPinQuery.sql"])
internal class SearchPinQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun search() {
        // WHEN
        val request = SearchPinRequest(
            userIds = listOf(111),
        )
        val response = rest.postForEntity(
            "/v1/pins/queries/search",
            request,
            SearchPinResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val pins = response.body!!.pins
        assertEquals(1, pins.size)
        assertEquals(100, pins[0].storyId)
        assertEquals(111, pins[0].userId)
        assertNotNull(pins[0].timestamp)
    }
}
