package com.wutsi.blog.pin.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType.UNPIN_STORY_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.pin.dto.UnpinStoryCommand
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.platform.core.stream.Event
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/pin/UnpinStoryCommand.sql"])
internal class UnpinStoryCommandTest {
    @Autowired
    private lateinit var eventHandler: RootEventHandler

    @Autowired
    private lateinit var userDao: UserRepository

    private fun unpin(storyId: Long) {
        eventHandler.handle(
            Event(
                type = UNPIN_STORY_COMMAND,
                payload = ObjectMapper().writeValueAsString(
                    UnpinStoryCommand(
                        storyId = storyId,
                    ),
                ),
            ),
        )
    }

    @Test
    fun unpinStory() {
        // WHEN
        unpin(100)

        Thread.sleep(10000L)

        val user = userDao.findById(111).get()
        assertNull(user.pinStoryId)
        assertNull(user.pinDateTime)
    }

    @Test
    fun unpinNotPinned() {
        // WHEN
        unpin(200)

        Thread.sleep(10000L)

        val user = userDao.findById(211).get()
        assertNull(user.pinStoryId)
        assertNull(user.pinDateTime)
    }
}
