package com.wutsi.blog.follower

import com.wutsi.blog.EventHandler
import com.wutsi.blog.client.follower.CountFollowerResponse
import com.wutsi.blog.client.follower.CreateFollowerRequest
import com.wutsi.blog.client.follower.CreateFollowerResponse
import com.wutsi.blog.client.follower.SearchFollowerResponse
import com.wutsi.blog.follower.dao.FollowerRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/FollowerController.sql"])
class FollowerControllerTest {

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: FollowerRepository

    @Autowired
    lateinit var events: EventHandler

    @BeforeEach
    fun setUp() {
        events.init()
    }

    @Test
    fun create() {
        val request = CreateFollowerRequest(
            followerUserId = 30L,
            userId = 40L,
        )
        val response = rest.postForEntity("/v1/followers", request, CreateFollowerResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val followerId = response.body!!.followerId
        val follower = dao.findById(followerId)
        assertTrue(follower.isPresent)
        assertEquals(request.userId, follower.get().userId)
        assertEquals(request.followerUserId, follower.get().followerUserId)
    }

    @Test
    fun createWithDuplicateFollower() {
        val request = CreateFollowerRequest(
            followerUserId = 31L,
            userId = 41L,
        )

        rest.postForEntity("/v1/followers", request, CreateFollowerResponse::class.java)
        val response = rest.postForEntity("/v1/followers", request, CreateFollowerResponse::class.java)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
    }

    @Test
    fun createSendEvent() {
        val request = CreateFollowerRequest(
            followerUserId = 32L,
            userId = 42L,
        )
        rest.postForEntity("/v1/followers", request, CreateFollowerResponse::class.java)

        val event = events.followEvent
        assertNotNull(event)
        assertEquals(request.userId, event?.userId)
        assertEquals(request.followerUserId, event?.followerUserId)
    }

    @Test
    fun createWithUserIdEqualFollowerUserId() {
        val request = CreateFollowerRequest(
            followerUserId = 30L,
            userId = 30L,
        )
        val response = rest.postForEntity("/v1/followers", request, CreateFollowerResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }

    @Test
    fun createWithInvalidFollowerId() {
        val request = CreateFollowerRequest(
            followerUserId = 9999L,
            userId = 1L,
        )
        val response = rest.postForEntity("/v1/followers", request, CreateFollowerResponse::class.java)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
    }

    @Test
    fun createWithInvalidUserId() {
        val request = CreateFollowerRequest(
            followerUserId = 1L,
            userId = 9999L,
        )
        val response = rest.postForEntity("/v1/followers", request, CreateFollowerResponse::class.java)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
    }

    @Test
    fun delete() {
        rest.delete("/v1/followers/12")

        val follower = dao.findById(12L)
        assertFalse(follower.isPresent)
    }

    @Test
    fun deleteSendEvent() {
        rest.delete("/v1/followers/13")

        val event = events.unfollowEvent
        assertNotNull(event)
        assertEquals(1L, event?.userId)
        assertEquals(41L, event?.followerUserId)
    }

    @Test
    fun searchFollower() {
        val response = rest.getForEntity("/v1/followers?userId=6&followerUserId=3", SearchFollowerResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val followers = response.body!!.followers

        assertEquals(1, followers.size)
        assertEquals(1, followers[0].id)
        assertEquals(6L, followers[0].userId)
        assertEquals(3L, followers[0].followerUserId)
    }

    @Test
    fun count() {
        val response = rest.getForEntity("/v1/followers/count?userId=6", CountFollowerResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val counts = response.body!!.counts.sortedBy { it.userId }

        assertEquals(4L, counts[0].value)
    }
}
