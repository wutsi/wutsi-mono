package com.wutsi.blog.story.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.event.EventType.VIEW_STORY_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.story.dao.ViewRepository
import com.wutsi.blog.story.dto.ViewStoryCommand
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.core.tracing.TracingContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cache.Cache
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ViewStoryCommandTest {
    @Autowired
    private lateinit var eventHandler: RootEventHandler

    @Autowired
    private lateinit var cache: Cache

    @Autowired
    private lateinit var viewDao: ViewRepository

    @MockBean
    private lateinit var traceContext: TracingContext

    private val deviceId = "the-device-id"

    @BeforeEach
    fun setUp() {
        cache.clear()

        doReturn(deviceId).whenever(traceContext).deviceId()
    }

    @Test
    fun addByUserId() {
        eventHandler.handle(
            Event(
                type = VIEW_STORY_COMMAND,
                payload = ObjectMapper().writeValueAsString(
                    ViewStoryCommand(
                        userId = 1L,
                        deviceId = deviceId,
                        storyId = 11L,
                    ),
                ),
            ),
        )

        val result = viewDao.findStoryIdsByUserIdOrDeviceId(1L, deviceId)
        assertEquals(listOf(11L), result)
    }

    @Test
    fun addByDeviceId() {
        eventHandler.handle(
            Event(
                type = VIEW_STORY_COMMAND,
                payload = ObjectMapper().writeValueAsString(
                    ViewStoryCommand(
                        userId = null,
                        deviceId = deviceId,
                        storyId = 11L,
                    ),
                ),
            ),
        )
        eventHandler.handle(
            Event(
                type = VIEW_STORY_COMMAND,
                payload = ObjectMapper().writeValueAsString(
                    ViewStoryCommand(
                        userId = null,
                        deviceId = deviceId,
                        storyId = 12L,
                    ),
                ),
            ),
        )

        val result = viewDao.findStoryIdsByUserIdOrDeviceId(null, deviceId)
        assertEquals(listOf(11L, 12L), result)
    }
}
