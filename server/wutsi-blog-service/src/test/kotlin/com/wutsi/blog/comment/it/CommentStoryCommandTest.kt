package com.wutsi.blog.comment.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.comment.dao.CommentRepository
import com.wutsi.blog.comment.dao.CommentStoryRepository
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.event.EventType.COMMENT_STORY_COMMAND
import com.wutsi.blog.event.RootEventHandler
import com.wutsi.platform.core.stream.Event
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/comment/CommentStoryCommand.sql"])
internal class CommentStoryCommandTest {
    @Autowired
    private lateinit var eventHandler: RootEventHandler

    @Autowired
    private lateinit var commentDao: CommentRepository

    @Autowired
    private lateinit var storyDao: CommentStoryRepository

    private fun comment(storyId: Long, userId: Long, text: String) {
        eventHandler.handle(
            Event(
                type = COMMENT_STORY_COMMAND,
                payload = ObjectMapper().writeValueAsString(
                    CommentStoryCommand(
                        storyId = storyId,
                        userId = userId,
                        text = text,
                    ),
                ),
            ),
        )
    }

    @Test
    fun commentStory() {
        // WHEN
        comment(100, 111, "This is a comment")

        Thread.sleep(15000L)

        val like = commentDao.findByStoryIdAndUserId(100, 111)
        assertNotNull(like?.eventId)

        val story = storyDao.findById(100)
        assertEquals(1001, story.get().count)
    }
}
