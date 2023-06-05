package com.wutsi.blog.comment.it

import com.wutsi.blog.comment.dto.CountCommentRequest
import com.wutsi.blog.comment.dto.CountCommentResponse
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
@Sql(value = ["/db/clean.sql", "/db/comment/CountCommentQuery.sql"])
internal class CountCommentQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun `by user who commented the story`() {
        // WHEN
        val request = CountCommentRequest(
            storyIds = listOf(100),
            userId = 111,
        )
        val response = rest.postForEntity(
            "/v1/comments/queries/count",
            request,
            CountCommentResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val comments = response.body!!.counters
        assertEquals(1, comments.size)
        assertEquals(100, comments[0].storyId)
        assertEquals(1000, comments[0].count)
        assertTrue(comments[0].commented)
    }

    @Test
    fun `user who did not commented the story`() {
        // WHEN
        val request = CountCommentRequest(
            storyIds = listOf(100),
            userId = 999,
        )
        val response = rest.postForEntity(
            "/v1/comments/queries/count",
            request,
            CountCommentResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val comments = response.body!!.counters
        assertEquals(1, comments.size)
        assertEquals(100, comments[0].storyId)
        assertEquals(1000, comments[0].count)
        assertFalse(comments[0].commented)
    }

    @Test
    fun `anonymous`() {
        // WHEN
        val request = CountCommentRequest(
            storyIds = listOf(100),
            userId = null,
        )
        val response = rest.postForEntity(
            "/v1/comments/queries/count",
            request,
            CountCommentResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val comments = response.body!!.counters
        assertEquals(1, comments.size)
        assertEquals(100, comments[0].storyId)
        assertEquals(1000, comments[0].count)
        assertFalse(comments[0].commented)
    }

    @Test
    fun `invalid story`() {
        // WHEN
        val request = CountCommentRequest(
            storyIds = listOf(999, 990),
            userId = 999,
        )
        val response = rest.postForEntity(
            "/v1/comments/queries/count",
            request,
            CountCommentResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val comments = response.body!!.counters
        assertEquals(0, comments.size)
    }
}
