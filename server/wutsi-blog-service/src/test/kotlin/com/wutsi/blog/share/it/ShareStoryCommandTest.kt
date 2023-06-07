package com.wutsi.blog.share.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.event.EventType.SHARE_STORY_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.blog.share.dao.ShareRepository
import com.wutsi.blog.share.dto.ShareStoryCommand
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.platform.core.stream.Event
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/share/ShareStoryCommand.sql"])
internal class ShareStoryCommandTest {
    @Autowired
    private lateinit var eventHandler: RootEventHandler

    @Autowired
    private lateinit var storyDao: StoryRepository

    @Autowired
    private lateinit var shareDao: ShareRepository

    private fun share(storyId: Long, userId: Long?) {
        eventHandler.handle(
            Event(
                type = SHARE_STORY_COMMAND,
                payload = ObjectMapper().writeValueAsString(
                    ShareStoryCommand(
                        storyId = storyId,
                        userId = userId,
                    ),
                ),
            ),
        )
    }

    @Test
    fun share() {
        // WHEN
        share(100L, null)

        Thread.sleep(10000L)

        val shares = shareDao.findByStoryId(100L)
        assertEquals(5, shares.size)

        val story = storyDao.findById(100)
        assertEquals(5, story.get().shareCount)
    }

    @Test
    fun shareFirst() {
        // WHEN
        share(200, 211)

        Thread.sleep(10000L)

        val shares = shareDao.findByStoryId(200L)
        assertEquals(1, shares.size)

        val story = storyDao.findById(200)
        assertEquals(1, story.get().shareCount)
    }
}
