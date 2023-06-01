package com.wutsi.blog.like.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.event.EventType.LIKE_STORY_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.like.dao.LikeRepository
import com.wutsi.blog.like.dao.LikeStoryRepository
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.core.tracing.TracingContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import java.util.UUID
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/like/LikeStoryCommand.sql"])
internal class LikeStoryCommandTest {
    @Autowired
    private lateinit var eventHandler: RootEventHandler

    @Autowired
    private lateinit var likeDao: LikeRepository

    @Autowired
    private lateinit var storyDao: LikeStoryRepository

    @MockBean
    private lateinit var tracingContext: TracingContext

    private val deviceId: String = "device-like"

    @BeforeEach
    fun setUp() {
        // GIVEN
        val traceId = UUID.randomUUID().toString()
        doReturn(deviceId).whenever(tracingContext).deviceId()
        doReturn("TEST").whenever(tracingContext).clientId()
        doReturn(traceId).whenever(tracingContext).traceId()
    }

    private fun like(storyId: Long, userId: Long?) {
        eventHandler.handle(
            Event(
                type = LIKE_STORY_COMMAND,
                payload = ObjectMapper().writeValueAsString(
                    LikeStoryCommand(
                        storyId = storyId,
                        userId = userId,
                        deviceId = deviceId,
                    ),
                ),
            ),
        )
    }

    @Test
    fun likeByUserId() {
        // WHEN
        like(100, 111)

        Thread.sleep(10000L)

        val like = likeDao.findByStoryIdAndUserId(100, 111)
        assertNotNull(like)
        assertNull(like.deviceId)

        val story = storyDao.findById(100)
        assertEquals(5, story.get().count)
    }

    @Test
    fun likeByDeviceId() {
        // WHEN
        like(
            storyId = 101L,
            userId = null,
        )

        // THEN
        Thread.sleep(10000L)

        val like = likeDao.findByStoryIdAndDeviceId(101, deviceId)
        assertNotNull(like)
        assertNull(like.userId)

        val story = storyDao.findById(101)
        assertEquals(1, story.get().count)
    }

    @Test
    fun likeDuplicateUserId() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        like(
            storyId = 200L,
            userId = 211L,
        )

        // THEN
        Thread.sleep(10000L)

        val like = likeDao.findByStoryIdAndUserId(200, 211)
        assertEquals(true, like?.timestamp?.before(now))
    }

    @Test
    fun likeDuplicateDeviceId() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        like(
            storyId = 200L,
            userId = null,
        )

        // THEN
        Thread.sleep(10000L)

        val like = likeDao.findByStoryIdAndDeviceId(200, deviceId)
        assertEquals(true, like?.timestamp?.before(now))
    }
}
