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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/comment/SearchCommentQuery.sql"])
internal class SearchCommentQueryTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun search() {
        // WHEN
        val request = SearchCommentRequest(
            storyId = 100L,
            limit = 20,
        )
        val response = rest.postForEntity(
            "/v1/comments/queries/search",
            request,
            SearchCommentResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val comments = response.body!!.comments
        assertEquals(2, comments.size)

        assertEquals(100, comments[0].storyId)
        assertEquals(211, comments[0].userId)
        assertEquals("World", comments[0].text)

        assertEquals(100, comments[1].storyId)
        assertEquals(111, comments[1].userId)
        assertEquals("Hello", comments[1].text)
    }

    @Test
    fun `limit is zero`() {
        // WHEN
        val request = SearchCommentRequest(
            storyId = 100L,
            limit = 0,
        )
        val response = rest.postForEntity(
            "/v1/comments/queries/search",
            request,
            SearchCommentResponse::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val comments = response.body!!.comments
        assertEquals(2, comments.size)

        assertEquals(100, comments[0].storyId)
        assertEquals(211, comments[0].userId)
        assertEquals("World", comments[0].text)

        assertEquals(100, comments[1].storyId)
        assertEquals(111, comments[1].userId)
        assertEquals("Hello", comments[1].text)
    }
}
