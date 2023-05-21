package com.wutsi.blog.like.it

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.like.dto.SearchLikeResponse
import com.wutsi.platform.core.tracing.TracingContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/like/SearchQuery.sql"])
internal class SearchQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var tracingContext: TracingContext

    private val deviceId: String = "device-search"

    @BeforeEach
    fun setUp() {
        // GIVEN
        val traceId = UUID.randomUUID().toString()
        doReturn(deviceId).whenever(tracingContext).deviceId()
        doReturn("TEST").whenever(tracingContext).clientId()
        doReturn(traceId).whenever(tracingContext).traceId()
    }

    @Test
    fun `search by user who liked the story`() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/likes/queries/search?story-id=100&user-id=111",
            SearchLikeResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val likes = response.body!!.likes
        assertEquals(1, likes.size)
        assertEquals(100, likes[0].storyId)
        assertEquals(1000, likes[0].count)
        assertTrue(likes[0].liked)
    }

    @Test
    fun `search by user who did not liked the story`() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/likes/queries/search?story-id=100&user-id=999",
            SearchLikeResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val likes = response.body!!.likes
        assertEquals(1, likes.size)
        assertEquals(100, likes[0].storyId)
        assertEquals(1000, likes[0].count)
        assertFalse(likes[0].liked)
    }

    @Test
    fun `search by device who liked the story`() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/likes/queries/search?story-id=100&device-id=$deviceId",
            SearchLikeResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val likes = response.body!!.likes
        assertEquals(1, likes.size)
        assertEquals(100, likes[0].storyId)
        assertEquals(1000, likes[0].count)
        assertTrue(likes[0].liked)
    }

    @Test
    fun `search by device who did not liked the story`() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/likes/queries/search?story-id=100&device-id=xxxx",
            SearchLikeResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val likes = response.body!!.likes
        assertEquals(1, likes.size)
        assertEquals(100, likes[0].storyId)
        assertEquals(1000, likes[0].count)
        assertFalse(likes[0].liked)
    }

    @Test
    fun `search multiple by user`() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/likes/queries/search?story-id=100&story-id=101&story-id=200&user-id=111",
            SearchLikeResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val likes = response.body!!.likes.sortedBy { it.storyId }
        assertEquals(2, likes.size)
        assertEquals(100, likes[0].storyId)
        assertEquals(1000, likes[0].count)
        assertTrue(likes[0].liked)

        assertEquals(101, likes[1].storyId)
        assertEquals(13, likes[1].count)
        assertFalse(likes[1].liked)
    }

    @Test
    fun `search multiple by device`() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/likes/queries/search?story-id=100&story-id=101&story-id=200&device-id=$deviceId",
            SearchLikeResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val likes = response.body!!.likes.sortedBy { it.storyId }
        assertEquals(2, likes.size)
        assertEquals(100, likes[0].storyId)
        assertEquals(1000, likes[0].count)
        assertTrue(likes[0].liked)

        assertEquals(101, likes[1].storyId)
        assertEquals(13, likes[1].count)
        assertTrue(likes[1].liked)
    }
}
