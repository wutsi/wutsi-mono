package com.wutsi.blog.pin.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType.PIN_STORY_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.platform.core.stream.Event
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.util.Date

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/pin/PinStoryCommand.sql"])
internal class PinStoryCommandTest {
    @Autowired
    private lateinit var eventHandler: RootEventHandler

    @Autowired
    private lateinit var userDao: UserRepository

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
        // GIVEN
        val now = Date()
        Thread.sleep(2000)

        // WHEN
        pin(100)

        Thread.sleep(10000L)

        val user = userDao.findById(111)
        assertEquals(100, user.get().pinStoryId)
        assertEquals(true, user.get().pinDateTime?.after(now))
    }

    @Test
    fun pinAnotherStory() {
        // GIVEN
        val now = Date()
        Thread.sleep(2000)

        // WHEN
        pin(201)

        // THEN
        Thread.sleep(10000L)

        val user = userDao.findById(211)
        assertEquals(201, user.get().pinStoryId)
        assertEquals(true, user.get().pinDateTime?.after(now))
    }
}
