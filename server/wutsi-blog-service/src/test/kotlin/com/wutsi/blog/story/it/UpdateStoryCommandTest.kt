package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ResourceHelper
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.google.gemini.ai.GCandidate
import com.wutsi.blog.google.gemini.ai.GContent
import com.wutsi.blog.google.gemini.ai.GPart
import com.wutsi.blog.google.gemini.ai.Gemini
import com.wutsi.blog.google.gemini.ai.GenerateContentResponse
import com.wutsi.blog.story.dao.StoryContentRepository
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dao.TagRepository
import com.wutsi.blog.story.dto.CreateStoryResponse
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.StoryUpdatedEventPayload
import com.wutsi.blog.story.dto.UpdateStoryCommand
import com.wutsi.event.store.EventStore
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/story/UpdateStoryCommand.sql"])
class UpdateStoryCommandTest : ClientHttpRequestInterceptor {
    @Autowired
    private lateinit var eventStore: EventStore

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var storyDao: StoryRepository

    @Autowired
    private lateinit var contentDao: StoryContentRepository

    @Autowired
    private lateinit var tagDao: TagRepository

    @MockBean
    private lateinit var gemini: Gemini

    private var accessToken: String? = "session-ray"

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        accessToken?.let {
            request.headers.setBearerAuth(it)
        }
        return execution.execute(request, body)
    }

    @BeforeEach
    fun setUp() {
        rest.restTemplate.interceptors = listOf(this)

        doReturn(
            GenerateContentResponse(
                candidates = listOf(
                    GCandidate(
                        content = GContent(
                            parts = listOf(
                                GPart("1. COVID-19\n2. test")
                            )
                        )
                    )
                )
            )
        ).doReturn(
                GenerateContentResponse(
                    candidates = listOf(
                        GCandidate(
                            content = GContent(
                                parts = listOf(
                                    GPart("Summary of publish")
                                )
                            )
                        )
                    )
                )
            ).whenever(gemini)
            .generateContent(any())
    }

    @Test
    fun createContent() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val command = UpdateStoryCommand(
            storyId = 1L,
            title = "Hello",
            content = ResourceHelper.loadResourceAsString("/editorjs.json"),
        )

        val result = rest.postForEntity("/v1/stories/commands/update", command, CreateStoryResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertEquals(story.title, story.title)
        assertEquals(48, story.wordCount)
        assertEquals(
            "This is the toolkit library from which all other modules inherit functionality. It also includes the core facades for the Tika API.",
            story.summary,
        )
        assertEquals(1, story.readingMinutes)
        assertEquals("en", story.language)
        assertEquals(StoryStatus.DRAFT, story.status)
        assertEquals("/upload/temporary/o_488cfb382712d6af914301c73f376e8c.jpg", story.thumbnailUrl)
        assertTrue(story.modificationDateTime.after(now))

        val content = contentDao.findByStory(story)
        assertEquals(1, content.size)
        assertEquals(story.title, content[0].title)
        assertEquals(story.summary, content[0].summary)
        assertEquals(command.content, content[0].content)
        assertEquals(story.language, content[0].language)
        assertTrue(content[0].modificationDateTime.after(now))

        val event = eventStore
            .events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_UPDATED_EVENT,
        ).last()
        val payload = event.payload as StoryUpdatedEventPayload
        assertEquals(command.title, payload.title)
        assertEquals(command.content, payload.content)
    }

    @Test
    fun updateContentOfPublishedStory() {
        // WHEN
        val command = UpdateStoryCommand(
            storyId = 2L,
            title = "Hello",
            content = ResourceHelper.loadResourceAsString("/editorjs.json"),
        )

        val result = rest.postForEntity("/v1/stories/commands/update", command, CreateStoryResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertEquals(story.title, story.title)
        assertEquals(48, story.wordCount)
        assertEquals(1, story.readingMinutes)
        assertEquals("en", story.language)
        assertEquals(StoryStatus.PUBLISHED, story.status)
        assertEquals("/upload/temporary/o_488cfb382712d6af914301c73f376e8c.jpg", story.thumbnailUrl)

        val content = contentDao.findByStory(story)
        assertEquals(1, content.size)
        assertEquals(story.title, content[0].title)
        assertEquals(command.content, content[0].content)
        assertEquals(story.language, content[0].language)

        val event = eventStore
            .events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_UPDATED_EVENT,
        ).last()
        val payload = event.payload as StoryUpdatedEventPayload
        assertEquals(command.title, payload.title)
        assertEquals(command.content, payload.content)

        Thread.sleep(15000)
        val story1 = storyDao.findById(command.storyId).get()
        assertNotNull(story1.summary)

        val content1 = contentDao.findByStory(story1)[0]
        assertEquals(story1.summary, content1.summary)

        val tags = tagDao.findByNameIn(arrayListOf("covid-19", "test"))
        assertEquals(2, tags.size)
    }

    @Test
    fun noContent() {
        // WHEN
        val command = UpdateStoryCommand(
            storyId = 3L,
            title = "Hello. This is a nice article!",
            content = "",
        )

        val result = rest.postForEntity("/v1/stories/commands/update", command, CreateStoryResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertEquals(story.title, story.title)
        assertEquals(0, story.wordCount)
        assertEquals("This is summary", story.summary)
        assertEquals(0, story.readingMinutes)
        assertEquals("", story.language)
        assertEquals(StoryStatus.DRAFT, story.status)
        assertEquals("", story.thumbnailUrl)

        val content = contentDao.findByStoryAndLanguage(story, story.language)
        assertTrue(content.isEmpty)

        val event = eventStore
            .events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_UPDATED_EVENT,
        ).last()
        val payload = event.payload as StoryUpdatedEventPayload
        assertEquals(command.title, payload.title)
        assertEquals(command.content, payload.content)
    }

    @Test
    fun error403() {
        // WHEN
        val command = UpdateStoryCommand(
            storyId = 20L,
            title = "Hello",
        )

        val result = rest.postForEntity("/v1/stories/commands/update", command, CreateStoryResponse::class.java)
        assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }
}
