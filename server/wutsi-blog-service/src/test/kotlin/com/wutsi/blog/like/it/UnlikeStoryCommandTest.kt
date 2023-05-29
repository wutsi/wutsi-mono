package com.wutsi.blog.like.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.event.EventType.UNLIKE_STORY_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.like.dao.LikeRepository
import com.wutsi.blog.like.dao.LikeStoryRepository
import com.wutsi.blog.like.dto.UnlikeStoryCommand
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.core.tracing.TracingContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/like/UnlikeStoryCommand.sql"])
internal class UnlikeStoryCommandTest {
    @Autowired
    private lateinit var eventHandler: RootEventHandler

    @Autowired
    private lateinit var likeDao: LikeRepository

    @Autowired
    private lateinit var storyDao: LikeStoryRepository

    @MockBean
    private lateinit var tracingContext: TracingContext

    private val deviceId: String = "device-unlike"

    @BeforeEach
    fun setUp() {
        // GIVEN
        val traceId = UUID.randomUUID().toString()
        doReturn(deviceId).whenever(tracingContext).deviceId()
        doReturn("TEST").whenever(tracingContext).clientId()
        doReturn(traceId).whenever(tracingContext).traceId()
    }

    private fun unlike(storyId: Long, userId: Long?) {
        eventHandler.handle(
            Event(
                type = UNLIKE_STORY_COMMAND,
                payload = ObjectMapper().writeValueAsString(
                    UnlikeStoryCommand(
                        storyId = storyId,
                        userId = userId,
                        deviceId = deviceId,
                    ),
                ),
            ),
        )
    }

    @Test
    fun unlikeByUserId() {
        // WHEN
        unlike(
            storyId = 100L,
            userId = 111L,
        )

        // THEN
        Thread.sleep(15000L)

        val like = likeDao.findByStoryIdAndUserId(100, 111)
        assertNull(like)

        val story = storyDao.findById(100)
        assertEquals(4, story.get().count)
    }

    @Test
    fun unlikeByDeviceId() {
        // WHEN
        unlike(
            storyId = 200L,
            userId = null,
        )

        // THEN
        Thread.sleep(15000L)

        val like = likeDao.findByStoryIdAndDeviceId(200L, deviceId)
        assertNull(like)

        val story = storyDao.findById(200L)
        assertEquals(1, story.get().count)
    }
}
