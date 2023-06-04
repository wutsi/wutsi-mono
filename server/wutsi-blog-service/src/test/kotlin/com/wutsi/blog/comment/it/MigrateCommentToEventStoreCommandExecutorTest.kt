package com.wutsi.blog.comment.it

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.event.EventType.COMMENT_STORY_COMMAND
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/comment/MigrateCommentToEventStoreCommand.sql"])
class MigrateCommentToEventStoreCommandTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @MockBean
    private lateinit var eventStream: EventStream

    @Test
    fun migrate() {
        // WHEN
        val response = rest.getForEntity(
            "/v1/comments/commands/migrate-to-event-stream",
            Any::class.java,
        )

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        Thread.sleep(1000)
        val payload = argumentCaptor<CommentStoryCommand>()
        verify(eventStream, times(2)).enqueue(eq(COMMENT_STORY_COMMAND), payload.capture())

        assertEquals(1L, payload.firstValue.storyId)
        assertEquals(101L, payload.firstValue.userId)
        assertEquals("Hello", payload.firstValue.text)

        assertEquals(2L, payload.secondValue.storyId)
        assertEquals(202L, payload.secondValue.userId)
        assertEquals("World", payload.secondValue.text)
    }
}
