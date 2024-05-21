package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ResourceHelper
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.google.gemini.ai.GCandidate
import com.wutsi.blog.google.gemini.ai.GContent
import com.wutsi.blog.google.gemini.ai.GPart
import com.wutsi.blog.google.gemini.ai.GPromptFeedback
import com.wutsi.blog.google.gemini.ai.GSafetyRating
import com.wutsi.blog.google.gemini.ai.Gemini
import com.wutsi.blog.google.gemini.ai.GenerateContentResponse
import com.wutsi.blog.story.dao.StoryContentRepository
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dao.TagRepository
import com.wutsi.blog.story.dto.PublishStoryCommand
import com.wutsi.blog.story.dto.StoryAccess
import com.wutsi.blog.story.dto.StoryPublicationScheduledEventPayload
import com.wutsi.blog.story.dto.StoryPublishedEventPayload
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.StoryUpdatedEventPayload
import com.wutsi.blog.story.dto.WPPValidation
import com.wutsi.blog.story.service.WPPService
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.blog.util.DateUtils
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Assertions.assertFalse
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/story/PublishStoryCommand.sql"])
class PublishStoryCommandTest : ClientHttpRequestInterceptor {
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

    @Autowired
    private lateinit var userDao: UserRepository

    @Autowired
    private lateinit var storage: StorageService

    @MockBean
    private lateinit var wppService: WPPService

    private var accessToken: String? = "session-ray"

    @MockBean
    private lateinit var gemini: Gemini

    private val wpp = WPPValidation(
        score = 55
    )

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

        doReturn(wpp).whenever(wppService).validate(any())

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
        )
            .doReturn(
                GenerateContentResponse(
                    candidates = listOf(
                        GCandidate(
                            content = GContent(
                                parts = listOf(
                                    GPart("Summary of publish")
                                )
                            )
                        )
                    ),
                    promptFeedback = GPromptFeedback(
                        safetyRatings = listOf(
                            GSafetyRating(
                                category = "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                                probability = "HIGH"
                            )
                        )
                    )
                )
            )
            .whenever(gemini).generateContent(any())
    }

    private fun initContent(id: Long) {
        val story = storyDao.findById(id).get()
        contentDao.findByStory(story).forEach { content ->
            contentDao.save(
                content.copy(content = ResourceHelper.loadResourceAsString("/editorjs.json"))
            )
        }
    }

    @Test
    fun publish() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        initContent(1L)

        // WHEN
        val command = PublishStoryCommand(
            storyId = 1L,
            title = "Publish me",
            tagline = "This is awesome!",
            categoryId = 1110L,
            access = StoryAccess.SUBSCRIBER,
        )
        val result = rest.postForEntity("/v1/stories/commands/publish", command, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertEquals(command.title, story.title)
        assertEquals(command.tagline, story.tagline)
        assertEquals(StoryStatus.PUBLISHED, story.status)
        assertEquals(command.categoryId, story.categoryId)
        assertEquals(command.access, story.access)
        assertEquals("/upload/temporary/o_488cfb382712d6af914301c73f376e8c.jpg", story.thumbnailUrl)
        assertEquals(wpp.score, story.wppScore)
        assertTrue(story.readabilityScore > 0)
        assertTrue(story.publishedDateTime!!.after(now))
        assertTrue(story.modificationDateTime.after(now))
        assertNull(story.scheduledPublishDateTime)

        val event = eventStore.events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_PUBLISHED_EVENT,
        ).last()
        assertEquals("1", event.userId)

        val payload = event.payload as StoryPublishedEventPayload
        assertEquals(command.access, payload.access)
        assertEquals(command.tagline, payload.tagline)
        assertEquals(command.title, payload.title)
        assertEquals(command.categoryId, payload.categoryId)

        Thread.sleep(15000)
        val user = userDao.findById(story.userId).get()
        assertEquals(5, user.storyCount)
        assertEquals(2, user.publishStoryCount)
        assertEquals(3, user.draftStoryCount)
        assertTrue(user.active)

        val url = storage.toURL("stories/${story.id}/bag-of-words.csv")
        assertTrue(storage.contains(url))

        val story1 = storyDao.findById(command.storyId).get()
        assertNotNull(story1.summary)
//        assertTrue(story1.sexuallyExplicitContent)

        val content1 = contentDao.findByStory(story1)[0]
        assertEquals(story1.summary, content1.summary)

        val tags = tagDao.findByNameIn(arrayListOf("covid-19", "test"))
        assertEquals(2, tags.size)
    }

    @Test
    fun republish() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        initContent(2L)

        // WHEN
        val command = PublishStoryCommand(
            storyId = 2L,
            title = "re-publish",
            tagline = "This is awesome!",
            categoryId = 1110L,
            access = StoryAccess.SUBSCRIBER,
        )

        val result = rest.postForEntity("/v1/stories/commands/publish", command, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertEquals(command.title, story.title)
        assertEquals(command.tagline, story.tagline)
        assertEquals(StoryStatus.PUBLISHED, story.status)
        assertEquals(command.access, story.access)
        assertEquals(command.categoryId, story.categoryId)
        assertEquals("/upload/temporary/o_488cfb382712d6af914301c73f376e8c.jpg", story.thumbnailUrl)
        assertEquals(wpp.score, story.wppScore)
        assertTrue(story.readabilityScore > 0)
        assertTrue(story.publishedDateTime!!.before(now))
        assertTrue(story.modificationDateTime.after(now))
        assertNull(story.scheduledPublishDateTime)

        val event = eventStore.events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_UPDATED_EVENT,
        ).last()
        assertEquals("1", event.userId)

        val payload = event.payload as StoryUpdatedEventPayload
        assertEquals(command.access, payload.access)
        assertEquals(command.tagline, payload.tagline)
        assertEquals(command.title, payload.title)
        assertEquals(command.categoryId, payload.categoryId)

        Thread.sleep(15000)
        val story1 = storyDao.findById(command.storyId).get()
        assertNotNull(story1.summary)

        val content1 = contentDao.findByStory(story1)[0]
        assertEquals(story1.summary, content1.summary)
    }

    @Test
    fun scheduled() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val command = PublishStoryCommand(
            storyId = 3L,
            title = "Schdule me",
            tagline = "This is awesome!",
            categoryId = 1110L,
            access = StoryAccess.SUBSCRIBER,
            scheduledPublishDateTime = DateUtils.addDays(Date(), 7),
        )

        val result = rest.postForEntity("/v1/stories/commands/publish", command, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertEquals(command.title, story.title)
        assertEquals(command.tagline, story.tagline)
        assertEquals(StoryStatus.DRAFT, story.status)
        assertEquals(command.categoryId, story.categoryId)
        assertEquals(command.access, story.access)
        assertTrue(story.publishedDateTime!!.before(now))
        assertTrue(story.modificationDateTime.after(now))
        assertEquals(
            DateUtils.beginingOfTheDay(command.scheduledPublishDateTime!!).time / 1000,
            DateUtils.beginingOfTheDay(story.scheduledPublishDateTime!!).time / 1000,
        )
        assertFalse(story.wpp)

        val event = eventStore.events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_PUBLICATION_SCHEDULED_EVENT,
        ).last()
        assertEquals("1", event.userId)

        val payload = event.payload as StoryPublicationScheduledEventPayload
        assertEquals(command.access, payload.access)
        assertEquals(command.tagline, payload.tagline)
        assertEquals(command.title, payload.title)
        assertEquals(command.categoryId, payload.categoryId)
        assertEquals(
            DateUtils.beginingOfTheDay(command.scheduledPublishDateTime!!).time / 1000,
            DateUtils.beginingOfTheDay(payload.scheduledPublishDateTime).time / 1000,
        )
    }

    @Test
    fun publishWithoutUpdating() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        initContent(4L)

        // WHEN
        val command = PublishStoryCommand(
            storyId = 4L,
        )

        val result = rest.postForEntity("/v1/stories/commands/publish", command, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertEquals("Draft", story.title)
        assertEquals("This is summary", story.summary)
        assertEquals("Sample Tagline", story.tagline)
        assertEquals(StoryStatus.PUBLISHED, story.status)
        assertEquals(100, story.topicId)
        assertEquals(StoryAccess.PUBLIC, story.access)
        assertTrue(story.publishedDateTime!!.after(now))
        assertTrue(story.modificationDateTime.after(now))
        assertNull(story.scheduledPublishDateTime)

        val tags = tagDao.findByNameIn(arrayListOf("covid-19"))
        assertEquals(1, tags.size)

        val event = eventStore.events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_PUBLISHED_EVENT,
        ).last()
        assertEquals("1", event.userId)

        val payload = event.payload as StoryPublishedEventPayload
        assertNull(payload.access)
        assertNull(payload.tagline)
        assertNull(payload.title)
        assertNull(payload.categoryId)
    }

    @Test
    fun emptyContent() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val command = PublishStoryCommand(
            storyId = 5L,
            title = "Foo",
            tagline = "This is a tagline",
            categoryId = 1100,
            access = StoryAccess.SUBSCRIBER
        )

        val result = rest.postForEntity("/v1/stories/commands/publish", command, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        Thread.sleep(15000)
        verify(gemini, never()).generateContent(any())
    }

    @Test
    fun error403() {
        // WHEN
        val command = PublishStoryCommand(
            storyId = 20L,
            title = "Schdule me",
            tagline = "This is awesome!",
            access = StoryAccess.SUBSCRIBER,
            scheduledPublishDateTime = DateUtils.addDays(Date(), 7),
        )

        val result = rest.postForEntity("/v1/stories/commands/publish", command, Any::class.java)
        assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }
}
