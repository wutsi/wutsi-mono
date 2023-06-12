package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.story.dao.StoryContentRepository
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dto.ImportStoryCommand
import com.wutsi.blog.story.dto.ImportStoryResponse
import com.wutsi.blog.story.dto.StoryImportFailedEventPayload
import com.wutsi.blog.story.dto.StoryImportedEventPayload
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.exception.ImportException
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.stream.EventStream
import org.jsoup.HttpStatusException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/story/ImportStoryCommand.sql"])
class ImportStoryCommandTest : ClientHttpRequestInterceptor {
    @LocalServerPort
    private lateinit var port: Integer

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
    }

    private fun import(url: String, userId: Long): ResponseEntity<ImportStoryResponse> =
        rest.postForEntity(
            "/v1/stories/commands/import",
            ImportStoryCommand(
                url = url,
                userId = userId,
                timestamp = System.currentTimeMillis(),
            ),
            ImportStoryResponse::class.java,
        )

    @Test
    fun import() {
        // WHEN
        val response = import("http://localhost:$port/blog", 1)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val story = storyDao.findById(response.body!!.storyId).get()
        assertEquals(
            "Yaoundé: on rencontre le sous-développement par les chemins qu’on emprunte pour l’éviter - Kamer Kongossa",
            story.title,
        )
        assertEquals(
            "Dans mon ancienne vie, je passais beaucoup de temps au bar, épicentre de la socialisation camerounaise, temple du kongossa et des infos déguisées en kongossa, pouls de la République reconnu d’utilité ...",
            story.summary,
        )
        assertEquals(StoryStatus.DRAFT, story.status)
        assertEquals("https://kamerkongossa.cm/wp-content/uploads/2020/01/bain-de-boue.jpg", story.thumbnailUrl)
        assertEquals(
            "https://kamerkongossa.cm/2020/01/07/a-yaounde-on-rencontre-le-sous-developpement-par-les-chemins-quon-emprunte-pour-leviter/",
            story.sourceUrl,
        )
        assertEquals("b2a86584736ef906f92ebfefab0e6fd8", story.sourceUrlHash)
        assertEquals("Kamer Kongossa", story.sourceSite)
        assertEquals(1321, story.wordCount)
        assertEquals(6, story.readingMinutes)
        assertEquals("fr", story.language)
        assertNotNull(story.creationDateTime)
        assertNotNull(story.modificationDateTime)
        assertNull(story.publishedDateTime)

        val content = contentDao.findByStory(story)[0]
        assertTrue(content.content!!.isNotEmpty())
        assertEquals("application/editorjs", content.contentType)
        assertEquals("fr", content.language)
        assertNotNull(story.creationDateTime)
        assertNotNull(story.modificationDateTime)

        val events = eventStore.events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_IMPORTED_EVENT,
        )
        assertEquals(1, events.size)
        assertEquals("1", events[0].userId)
        val payload = events[0].payload as StoryImportedEventPayload
        assertEquals("http://localhost:$port/blog", payload.url)

        verify(eventStream).enqueue(eq(EventType.STORY_IMPORTED_EVENT), any())
        verify(eventStream).publish(eq(EventType.STORY_IMPORTED_EVENT), any())
    }

    @Test
    fun alreadyImported() {
        val url =
            "https://nkowa.com/podcast-jokkolabs-douala-les-valeurs-qui-nous-tiennent-a-coeur-sont-la-culture-lagriculture-et-linnovation"
        val response = import(url, 1)
        assertEquals(HttpStatus.CONFLICT, response.statusCode)

        val events = eventStore.events(
            streamId = StreamId.STORY,
            entityId = "-1",
            type = EventType.STORY_IMPORT_FAILED_EVENT,
        )
        assertEquals(1, events.size)
        assertEquals("1", events[0].userId)
        assertNotNull(events[0].payload)

        val payload = events[0].payload as StoryImportFailedEventPayload
        assertNull(payload.statusCode)
        assertEquals(url, payload.url)
        assertEquals("story_already_imported", payload.message)
        assertEquals(ImportException::class.java.name, payload.exceptionClass)

        verify(eventStream).enqueue(eq(EventType.STORY_IMPORT_FAILED_EVENT), any())
        verify(eventStream).publish(eq(EventType.STORY_IMPORT_FAILED_EVENT), any())
    }

    @Test
    fun noContent() {
        val response = import("http://localhost:$port/blog/empty", 1)
        assertEquals(HttpStatus.CONFLICT, response.statusCode)

        val events = eventStore.events(
            streamId = StreamId.STORY,
            entityId = "-1",
            type = EventType.STORY_IMPORT_FAILED_EVENT,
        )
        assertEquals(1, events.size)
        assertEquals("1", events[0].userId)
        assertNotNull(events[0].payload)

        val payload = events[0].payload as StoryImportFailedEventPayload
        assertNull(payload.statusCode)
        assertEquals("http://localhost:$port/blog/empty", payload.url)
        assertEquals("no_content", payload.message)
        assertEquals(ConflictException::class.java.name, payload.exceptionClass)

        verify(eventStream).enqueue(eq(EventType.STORY_IMPORT_FAILED_EVENT), any())
        verify(eventStream).publish(eq(EventType.STORY_IMPORT_FAILED_EVENT), any())
    }

    @Test
    fun importError() {
        val response = import("http://localhost:$port/blog/404", 1)
        assertEquals(HttpStatus.CONFLICT, response.statusCode)

        val events = eventStore.events(
            streamId = StreamId.STORY,
            entityId = "-1",
            type = EventType.STORY_IMPORT_FAILED_EVENT,
        )
        assertEquals(1, events.size)
        assertEquals("1", events[0].userId)
        assertNotNull(events[0].payload)

        val payload = events[0].payload as StoryImportFailedEventPayload
        assertEquals(404, payload.statusCode)
        assertEquals("http://localhost:$port/blog/404", payload.url)
        assertEquals(HttpStatusException::class.java.name, payload.exceptionClass)

        verify(eventStream).enqueue(eq(EventType.STORY_IMPORT_FAILED_EVENT), any())
        verify(eventStream).publish(eq(EventType.STORY_IMPORT_FAILED_EVENT), any())
    }

    @Test
    fun error403() {
        val response = import("http://localhost:$port/blog", 2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }
}
