package com.wutsi.blog.comment

import com.wutsi.blog.EventHandler
import com.wutsi.blog.client.comment.CountCommentResponse
import com.wutsi.blog.client.comment.CreateCommentRequest
import com.wutsi.blog.client.comment.CreateCommentResponse
import com.wutsi.blog.client.comment.SearchCommentResponse
import com.wutsi.blog.client.comment.UpdateCommentRequest
import com.wutsi.blog.comment.dao.CommentRepository
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/CommentController.sql"])
class CommentControllerTest {
    @Autowired
    lateinit var events: EventHandler

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: CommentRepository

    @BeforeEach
    fun setUp() {
        events.init()
    }

    @Test
    fun create() {
        val request = CreateCommentRequest(
            storyId = 1L,
            userId = 1L,
            text = "Test Create",
        )
        val response = rest.postForEntity("/v1/comments", request, CreateCommentResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val commentId = response.body!!.commentId
        val comment = dao.findById(commentId).get()
        assertEquals(request.text, comment.text)
        assertEquals(request.storyId, comment.storyId)
        assertEquals(request.userId, comment.userId)
    }

    @Test
    fun createShouldFireEvent() {
        val request = CreateCommentRequest(
            storyId = 1L,
            userId = 1L,
            text = "Test Create with events",
        )
        val response = rest.postForEntity("/v1/comments", request, CreateCommentResponse::class.java)

        val commentId = response.body!!.commentId
        assertEquals(commentId, events.commentEvent?.commenId)
    }

    @Test
    fun update() {
        val request = UpdateCommentRequest(
            text = "Test Update",
        )
        val response = rest.postForEntity("/v1/comments/11", request, CreateCommentResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val commentId = response.body!!.commentId
        val comment = dao.findById(commentId).get()
        assertEquals(request.text, comment.text)
    }

    @Test
    fun updateInvalidComment() {
        val request = UpdateCommentRequest(
            text = "Test Update",
        )
        val response = rest.postForEntity("/v1/comments/9999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)

        val error = response.body!!.error
        assertEquals("comment_not_found", error.code)
    }

    @Test
    fun delete() {
        rest.delete("/v1/comments/12")

        val comment = dao.findById(12L)
        assertFalse(comment.isPresent)
    }

    @Test
    fun search() {
        val response = rest.getForEntity("/v1/comments?limit=5&offset=5&storyId=2", SearchCommentResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val comments = response.body!!.comments

        assertEquals(3, comments.size)
        assertEquals(27L, comments[2].id)
        assertEquals(26L, comments[1].id)
        assertEquals(25L, comments[0].id)
    }

    @Test
    fun searchSince() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val since = fmt.format(DateUtils.addDays(Date(), -30))
        val response =
            rest.getForEntity("/v1/comments?limit=5&offset=5&since=$since", SearchCommentResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val comments = response.body!!.comments

        assertEquals(5, comments.size)
    }

    @Test
    fun count() {
        val response = rest.getForEntity("/v1/comments/count?storyId=1&storyId=2", CountCommentResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val counts = response.body!!.counts.sortedBy { it.storyId }

        assertEquals(2, counts.size)

        assertEquals(1L, counts[0].storyId)
        assertEquals(3L, counts[0].value)

        assertEquals(2L, counts[1].storyId)
        assertEquals(8L, counts[1].value)
    }
}
