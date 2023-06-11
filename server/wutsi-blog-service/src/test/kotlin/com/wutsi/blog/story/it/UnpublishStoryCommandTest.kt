package com.wutsi.blog.story.it

import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.UnpublishStoryCommand
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/story/UnpublishStoryCommand.sql"])
class UnpublishStoryCommandTest {
    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var storyDao: StoryRepository

    @Autowired
    private lateinit var userDao: UserRepository

    @Test
    fun unpublishPublished() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val command = UnpublishStoryCommand(
            storyId = 1L,
        )

        val result = rest.postForEntity("/v1/stories/commands/unpublish", command, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertEquals(StoryStatus.DRAFT, story.status)
        assertTrue(story.modificationDateTime.after(now))

        val events = eventStore.events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_UNPUBLISHED_EVENT,
        )
        assertTrue(events.isNotEmpty())

        Thread.sleep(10000)
        val user = userDao.findById(story.userId).get()
        assertEquals(2, user.storyCount)
        assertEquals(0, user.publishStoryCount)
        assertEquals(2, user.draftStoryCount)
        assertTrue(user.modificationDateTime.after(now))
    }

    @Test
    fun unpublishDraft() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val command = UnpublishStoryCommand(
            storyId = 2L,
        )

        val result = rest.postForEntity("/v1/stories/commands/unpublish", command, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertEquals(StoryStatus.DRAFT, story.status)
        assertFalse(story.modificationDateTime.after(now))

        val events = eventStore.events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_UNPUBLISHED_EVENT,
        )
        assertFalse(events.isNotEmpty())

        Thread.sleep(10000)
        val user = userDao.findById(story.userId).get()
        assertFalse(user.modificationDateTime.after(now))
    }
}
