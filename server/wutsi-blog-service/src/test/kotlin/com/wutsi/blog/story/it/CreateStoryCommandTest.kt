package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.ResourceHelper
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.story.dao.StoryContentRepository
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dto.CreateStoryCommand
import com.wutsi.blog.story.dto.CreateStoryResponse
import com.wutsi.blog.story.dto.StoryCreatedEventPayload
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/story/CreateStoryCommand.sql"])
class CreateStoryCommandTest {
    @MockBean
    private lateinit var eventStream: EventStream

    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var storyDao: StoryRepository

    @Autowired
    private lateinit var contentDao: StoryContentRepository

    @Test
    fun create() {
        // WHEN
        val command = CreateStoryCommand(
            userId = 1L,
            title = "Hello",
            content = ResourceHelper.loadResourceAsString("/editorjs.json"),
        )

        val result = rest.postForEntity("/v1/stories/commands/create", command, CreateStoryResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(result.body!!.storyId).get()
        assertEquals(story.title, story.title)
        assertEquals(48, story.wordCount)
        assertEquals(
            "Hey. Meet the new Editor. On this page you can see it in action — try to edit this text",
            story.summary,
        )
        assertEquals(1, story.readingMinutes)
        assertEquals("en", story.language)
        assertEquals(StoryStatus.DRAFT, story.status)
        assertEquals("/upload/temporary/o_488cfb382712d6af914301c73f376e8c.jpg", story.thumbnailUrl)

        val content = contentDao.findByStory(story)
        assertEquals(1, content.size)
        assertEquals(story.title, content[0].title)
        assertEquals(story.summary, content[0].summary)
        assertEquals(command.content, content[0].content)
        assertEquals(story.language, content[0].language)

        val event = eventStore.events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_CREATED_EVENT,
        ).last()
        val payload = event.payload as StoryCreatedEventPayload
        assertEquals(command.title, payload.title)
        assertEquals(command.content, payload.content)

        verify(eventStream).enqueue(eq(EventType.STORY_CREATED_EVENT), any())
        verify(eventStream).publish(eq(EventType.STORY_CREATED_EVENT), any())
    }

    @Test
    fun noContent() {
        // WHEN
        val command = CreateStoryCommand(
            userId = 1L,
            title = "Hello",
            content = "",
        )

        val result = rest.postForEntity("/v1/stories/commands/create", command, CreateStoryResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(result.body!!.storyId).get()
        assertEquals(story.title, story.title)
        assertEquals(0, story.wordCount)
        assertEquals("", story.summary)
        assertEquals(0, story.readingMinutes)
        assertEquals("en", story.language)
        assertEquals(StoryStatus.DRAFT, story.status)
        assertEquals("", story.thumbnailUrl)

        val content = contentDao.findByStory(story)
        assertEquals(0, content.size)

        val event = eventStore.events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_CREATED_EVENT,
        ).last()
        val payload = event.payload as StoryCreatedEventPayload
        assertEquals(command.title, payload.title)
        assertEquals(command.content, payload.content)

        verify(eventStream).enqueue(eq(EventType.STORY_CREATED_EVENT), any())
        verify(eventStream).publish(eq(EventType.STORY_CREATED_EVENT), any())
    }
}
