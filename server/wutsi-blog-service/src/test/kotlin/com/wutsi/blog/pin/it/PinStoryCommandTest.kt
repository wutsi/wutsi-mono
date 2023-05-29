package com.wutsi.blog.pin.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType.PIN_STORY_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.pin.dao.PinStoryRepository
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.platform.core.stream.Event
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/pin/PinStoryCommand.sql"])
internal class PinStoryCommandTest {
    @Autowired
    private lateinit var eventHandler: RootEventHandler

    @Autowired
    private lateinit var storyDao: PinStoryRepository

    private fun pin(storyId: Long) {
        eventHandler.handle(
            Event(
                type = PIN_STORY_COMMAND,
                payload = ObjectMapper().writeValueAsString(
                    PinStoryCommand(
                        storyId = storyId,
                    ),
                ),
            ),
        )
    }

    @Test
    fun pinStory() {
        // WHEN
        val now = Date()
        pin(100)

        Thread.sleep(15000L)

        val story = storyDao.findById(111)
        assertEquals(100, story.get().storyId)
        assertTrue(story.get().timestamp.after(now))
    }

    @Test
    fun duplicatePin() {
        // WHEN
        val now = Date()
        Thread.sleep(1000)
        pin(300)

        Thread.sleep(15000L)

        val story = storyDao.findById(311)
        assertTrue(story.get().timestamp.before(now))
    }

    @Test
    fun pinAnotherStory() {
        // WHEN
        val now = Date()
        pin(201)

        // THEN
        Thread.sleep(15000L)

        val story = storyDao.findById(211)
        assertEquals(201, story.get().storyId)
        assertTrue(story.get().timestamp.after(now))
    }
}
