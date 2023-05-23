package com.wutsi.blog.like

import com.wutsi.blog.EventHandler
import com.wutsi.blog.client.comment.CreateCommentRequest
import com.wutsi.blog.client.like.CountLikeResponse
import com.wutsi.blog.client.like.CreateLikeRequest
import com.wutsi.blog.client.like.CreateLikeResponse
import com.wutsi.blog.client.like.SearchLikeResponse
import com.wutsi.blog.like.dao.LikeV0Repository
import com.wutsi.platform.core.tracing.TracingContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/LikeController.sql"])
class LikeControllerTest {
    @Autowired
    lateinit var events: EventHandler

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: LikeV0Repository

    @BeforeEach
    fun setUp() {
        events.init()
    }

    @Test
    fun `user likes a story`() {
        val request = CreateLikeRequest(
            storyId = 2L,
            userId = 10L,
        )
        val response = rest.postForEntity(
            "/v1/likes",
            request,
            CreateLikeResponse::class.java,
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val likeId = response.body!!.likeId
        val like = dao.findById(likeId).get()
        assertEquals(request.storyId, like.story.id)
        assertEquals(request.userId, like.user?.id)
        assertNull(like.deviceId)
    }

    @Test
    fun `anonymous user likes a story`() {
        val request = CreateLikeRequest(
            storyId = 2L,
            userId = null,
        )
        val deviceUID = UUID.randomUUID().toString()
        val headers = HttpHeaders()
        headers.add(TracingContext.HEADER_DEVICE_ID, deviceUID)

        val entity = HttpEntity<Any>(request, headers)
        val response = rest.exchange("/v1/likes", HttpMethod.POST, entity, CreateLikeResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val likeId = response.body!!.likeId
        val like = dao.findById(likeId).get()
        assertEquals(request.storyId, like.story.id)
        assertNull(like.user)
        assertEquals(deviceUID, like.deviceId)
    }

    @Test
    fun `event is fired when user likes a story`() {
        val request = CreateCommentRequest(
            storyId = 1L,
            userId = 1L,
        )
        val response = rest.postForEntity("/v1/likes", request, CreateLikeResponse::class.java)

        val likeId = response.body!!.likeId
        assertEquals(likeId, events.likeEvent?.likeId)
    }

    @Test
    fun delete() {
        rest.delete("/v1/likes/12")

        val like = dao.findById(12L)
        assertFalse(like.isPresent)
    }

    @Test
    fun count() {
        val response = rest.getForEntity("/v1/likes/count?storyId=1&storyId=2", CountLikeResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val counts = response.body!!.counts.sortedBy { it.storyId }

        assertEquals(2, counts.size)

        assertEquals(1L, counts[0].storyId)
        assertEquals(2L, counts[0].value)

        assertEquals(2L, counts[1].storyId)
        assertEquals(2L, counts[1].value)
    }

    @Test
    fun countLikesReceived() {
        val response = rest.getForEntity("/v1/likes/count?authorId=1", CountLikeResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val counts = response.body!!.counts.sortedBy { it.storyId }

        assertEquals(2, counts.size)

        assertEquals(1L, counts[0].storyId)
        assertEquals(2L, counts[0].value)

        assertEquals(2L, counts[1].storyId)
        assertEquals(2L, counts[1].value)
    }

    @Test
    fun searchByUserId() {
        val response = rest.getForEntity("/v1/likes?storyId=1&storyId=2&userId=2", SearchLikeResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val likes = response.body!!.likes

        assertEquals(2, likes.size)

        assertEquals(1L, likes[0].storyId)
        assertEquals(2L, likes[0].userId)
        assertNull(likes[0].deviceId)

        assertEquals(2L, likes[1].storyId)
        assertEquals(2L, likes[1].userId)
        assertEquals("xxx", likes[1].deviceId)
    }

    @Test
    fun searchByDevice() {
        val response = rest.getForEntity("/v1/likes?storyId=1&storyId=2&deviceId=xxx", SearchLikeResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val likes = response.body!!.likes

        assertTrue(likes.isNotEmpty())
//        assertEquals(2, likes.size)
//
//        assertEquals(2L, likes[0].storyId)
//        assertEquals(2L, likes[0].userId)
//        assertEquals("xxx", likes[0].deviceId)
//
//        assertEquals(1L, likes[1].storyId)
//        assertEquals(10L, likes[1].userId)
//        assertEquals("xxx", likes[1].deviceId)
    }
}
