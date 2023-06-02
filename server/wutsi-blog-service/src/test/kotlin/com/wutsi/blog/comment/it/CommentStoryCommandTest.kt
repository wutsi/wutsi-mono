package com.wutsi.blog.comment.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.comment.dao.CommentRepository
import com.wutsi.blog.comment.dao.CommentStoryRepository
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.event.EventType.COMMENT_STORY_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.stream.Event
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/comment/CommentStoryCommand.sql"])
internal class CommentStoryCommandTest {
    @Autowired
    private lateinit var eventHandler: RootEventHandler

    @Autowired
    private lateinit var commentDao: CommentRepository

    @Autowired
    private lateinit var storyDao: CommentStoryRepository

    @Autowired
    private lateinit var eventStore: EventStore

    private fun comment(storyId: Long, userId: Long, text: String) {
        eventHandler.handle(
            Event(
                type = COMMENT_STORY_COMMAND,
                payload = ObjectMapper().writeValueAsString(
                    CommentStoryCommand(
                        storyId = storyId,
                        userId = userId,
                        text = text,
                        timestamp = System.currentTimeMillis(),
                    ),
                ),
            ),
        )
    }

    @Test
    fun comment() {
        // WHEN
        val now = Date()
        Thread.sleep(1000)

        comment(100, 111, "This is a comment")

        Thread.sleep(10000L)

        val comment = commentDao.findByStoryId(100).last()
        assertEquals("This is a comment", comment.text)
        assertTrue(comment.timestamp.after(now))

        val story = storyDao.findById(100)
        assertEquals(5, story.get().count)
    }

    @Test
    fun empty() {
        // WHEN
        comment(200, 111, "")

        Thread.sleep(10000L)

        val comments = commentDao.findByStoryId(200)
        assertTrue(comments.isEmpty())
    }

    @Test
    fun space() {
        // WHEN
        comment(200, 111, "   ")

        Thread.sleep(10000L)

        val comments = commentDao.findByStoryId(200)
        assertTrue(comments.isEmpty())
    }
}
