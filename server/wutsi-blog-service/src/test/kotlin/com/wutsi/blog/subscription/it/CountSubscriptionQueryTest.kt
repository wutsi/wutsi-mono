package com.wutsi.blog.subscription.it

import com.wutsi.blog.subscription.dto.CountSubscriptionRequest
import com.wutsi.blog.subscription.dto.CountSubscriptionResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/subscription/CountSubscriptionQuery.sql"])
internal class CountSubscriptionQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun `with subscriber-id`() {
        // WHEN
        val request = CountSubscriptionRequest(
            userIds = listOf(1, 2, 3, 4),
            subscriberId = 10,
        )
        val response = rest.postForEntity(
            "/v1/subscriptions/queries/count",
            request,
            CountSubscriptionResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val comments = response.body!!.counters
        assertEquals(3, comments.size)

        assertEquals(1, comments[0].userId)
        assertEquals(4, comments[0].count)
        assertTrue(comments[0].subscribed)

        assertEquals(2, comments[1].userId)
        assertEquals(1, comments[1].count)
        assertTrue(comments[1].subscribed)

        assertEquals(3, comments[2].userId)
        assertEquals(3, comments[2].count)
        assertFalse(comments[2].subscribed)
    }

    @Test
    fun `no subscriber-id`() {
        // WHEN
        val request = CountSubscriptionRequest(
            userIds = listOf(1, 2, 3, 4),
            subscriberId = null,
        )
        val response = rest.postForEntity(
            "/v1/subscriptions/queries/count",
            request,
            CountSubscriptionResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val counters = response.body!!.counters
        assertEquals(3, counters.size)

        assertEquals(1, counters[0].userId)
        assertEquals(4, counters[0].count)
        assertFalse(counters[0].subscribed)

        assertEquals(2, counters[1].userId)
        assertEquals(1, counters[1].count)
        assertFalse(counters[1].subscribed)

        assertEquals(3, counters[2].userId)
        assertEquals(3, counters[2].count)
        assertFalse(counters[2].subscribed)
    }

    @Test
    fun `invalid user`() {
        // WHEN
        val request = CountSubscriptionRequest(
            userIds = listOf(999, 8888),
            subscriberId = 10,
        )
        val response = rest.postForEntity(
            "/v1/subscriptions/queries/count",
            request,
            CountSubscriptionResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val counters = response.body!!.counters
        assertEquals(0, counters.size)
    }
}
