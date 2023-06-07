package com.wutsi.blog.story.it

import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dto.DeleteStoryCommand
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.event.store.EventStore
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/story/DeleteStoryCommand.sql"])
class DeleteStoryCommandTest {
    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var storyDao: StoryRepository

    @Autowired
    private lateinit var userDao: UserRepository

    @Test
    fun delete() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val command = DeleteStoryCommand(
            storyId = 1L,
        )

        val result = rest.postForEntity("/v1/stories/commands/delete", command, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertTrue(story.deletedDateTime!!.after(now))
        assertTrue(story.deleted)

        val events = eventStore.events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_DELETED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(10000)
        val user = userDao.findById(story.userId).get()
        assertEquals(0, user.storyCount)
        assertEquals(0, user.publishStoryCount)
        assertEquals(0, user.draftStoryCount)
    }

    @Test
    fun alreadyDeleted() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val command = DeleteStoryCommand(
            storyId = 99L,
        )

        val result = rest.postForEntity("/v1/stories/commands/delete", command, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertTrue(story.deletedDateTime!!.before(now))
        assertTrue(story.deleted)
    }
}
