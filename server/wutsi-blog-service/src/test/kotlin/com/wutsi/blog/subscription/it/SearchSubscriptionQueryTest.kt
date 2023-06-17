package com.wutsi.blog.subscription.it

import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.subscription.dto.SearchSubscriptionResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/subscription/SearchSubscriptionQuery.sql"])
internal class SearchSubscriptionQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun byUserIds() {
        // WHEN
        val request = SearchSubscriptionRequest(
            userIds = listOf(1, 3),
        )
        val response =
            rest.postForEntity("/v1/subscriptions/query/search", request, SearchSubscriptionResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val subscriptions = response.body!!.subscriptions.sortedBy { it.userId }

        assertEquals(3, subscriptions.size)

        assertEquals(1L, subscriptions[0].userId)
        assertEquals(2L, subscriptions[0].subscriberId)

        assertEquals(1L, subscriptions[1].userId)
        assertEquals(3L, subscriptions[1].subscriberId)

        assertEquals(3L, subscriptions[2].userId)
        assertEquals(2L, subscriptions[2].subscriberId)
    }

    @Test
    fun bySubscriberIds() {
        // WHEN
        val request = SearchSubscriptionRequest(
            subscriberId = 2L,
        )
        val response =
            rest.postForEntity("/v1/subscriptions/query/search", request, SearchSubscriptionResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val subscriptions = response.body!!.subscriptions.sortedBy { it.userId }

        assertEquals(2, subscriptions.size)

        assertEquals(1L, subscriptions[0].userId)
        assertEquals(2L, subscriptions[0].subscriberId)

        assertEquals(3L, subscriptions[1].userId)
        assertEquals(2L, subscriptions[1].subscriberId)
    }
}
