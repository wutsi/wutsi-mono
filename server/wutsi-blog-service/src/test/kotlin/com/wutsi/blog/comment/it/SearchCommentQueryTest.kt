package com.wutsi.blog.comment.it

import com.wutsi.blog.comment.dto.SearchCommentRequest
import com.wutsi.blog.comment.dto.SearchCommentResponse
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
@Sql(value = ["/db/clean.sql", "/db/comment/SearchCommentQuery.sql"])
internal class SearchCommentQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun `search by user who comment the story`() {
        // WHEN
        val request = SearchCommentRequest(
            storyIds = listOf(100),
            userId = 111,
        )
        val response = rest.postForEntity(
            "/v1/comments/queries/search",
            request,
            SearchCommentResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val comments = response.body!!.comments
        assertEquals(1, comments.size)
        assertEquals(100, comments[0].storyId)
        assertEquals(1000, comments[0].count)
        assertTrue(comments[0].commented)
    }

    @Test
    fun `search by user who did not commentd the story`() {
        // WHEN
        val request = SearchCommentRequest(
            storyIds = listOf(100),
            userId = 999,
        )
        val response = rest.postForEntity(
            "/v1/comments/queries/search",
            request,
            SearchCommentResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val comments = response.body!!.comments
        assertEquals(1, comments.size)
        assertEquals(100, comments[0].storyId)
        assertEquals(1000, comments[0].count)
        assertFalse(comments[0].commented)
    }
}
