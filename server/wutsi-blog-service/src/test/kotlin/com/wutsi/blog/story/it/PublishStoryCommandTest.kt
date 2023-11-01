package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
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
    private lateinit var tagDao: TagRepository

    @Autowired
    private lateinit var userDao: UserRepository

    @Autowired
    private lateinit var storage: StorageService

    @MockBean
    private lateinit var wppService: WPPService

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

        doReturn(WPPValidation()).whenever(wppService).validate(any())
    }

    @Test
    fun publish() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val command = PublishStoryCommand(
            storyId = 1L,
            title = "Publish me",
            tagline = "This is awesome!",
            summary = "Summary of publish",
            topicId = 101L,
            tags = arrayListOf("COVID-19", "test"),
            access = StoryAccess.SUBSCRIBER,
        )

        val result = rest.postForEntity("/v1/stories/commands/publish", command, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertEquals(command.title, story.title)
        assertEquals(command.summary, story.summary)
        assertEquals(command.tagline, story.tagline)
        assertEquals(StoryStatus.PUBLISHED, story.status)
        assertEquals(command.topicId, story.topicId)
        assertEquals(command.access, story.access)
        assertTrue(story.publishedDateTime!!.after(now))
        assertTrue(story.modificationDateTime.after(now))
        assertNull(story.scheduledPublishDateTime)
        assertFalse(story.wpp)

        val tags = tagDao.findByNameIn(arrayListOf("covid-19", "test"))
        assertEquals(2, tags.size)

        val event = eventStore.events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_PUBLISHED_EVENT,
        ).last()
        assertEquals("1", event.userId)

        val payload = event.payload as StoryPublishedEventPayload
        assertEquals(command.access, payload.access)
        assertEquals(command.tagline, payload.tagline)
        assertEquals(command.summary, payload.summary)
        assertEquals(command.title, payload.title)
        assertEquals(command.tags, payload.tags)
        assertEquals(command.topicId, payload.topicId)

        Thread.sleep(10000)
        val user = userDao.findById(story.userId).get()
        assertEquals(4, user.storyCount)
        assertEquals(2, user.publishStoryCount)
        assertEquals(2, user.draftStoryCount)

        val url = storage.toURL("stories/${story.id}/bag-of-words.csv")
        assertTrue(storage.contains(url))
    }

    @Test
    fun republish() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

        // WHEN
        val command = PublishStoryCommand(
            storyId = 2L,
            title = "Publish me",
            tagline = "This is awesome!",
            summary = "Summary of publish",
            topicId = 101L,
            tags = arrayListOf("COVID-19", "test"),
            access = StoryAccess.SUBSCRIBER,
        )

        val result = rest.postForEntity("/v1/stories/commands/publish", command, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertEquals(command.title, story.title)
        assertEquals(command.summary, story.summary)
        assertEquals(command.tagline, story.tagline)
        assertEquals(StoryStatus.PUBLISHED, story.status)
        assertEquals(command.topicId, story.topicId)
        assertEquals(command.access, story.access)
        assertTrue(story.publishedDateTime!!.before(now))
        assertTrue(story.modificationDateTime.after(now))
        assertNull(story.scheduledPublishDateTime)
        assertFalse(story.wpp)

        val tags = tagDao.findByNameIn(arrayListOf("covid-19", "test"))
        assertEquals(2, tags.size)

        val event = eventStore.events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_UPDATED_EVENT,
        ).last()
        assertEquals("1", event.userId)

        val payload = event.payload as StoryUpdatedEventPayload
        assertEquals(command.access, payload.access)
        assertEquals(command.tagline, payload.tagline)
        assertEquals(command.summary, payload.summary)
        assertEquals(command.title, payload.title)
        assertEquals(command.tags, payload.tags)
        assertEquals(command.topicId, payload.topicId)
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
            summary = "Summary of publish",
            topicId = 101L,
            tags = arrayListOf("COVID-19", "test"),
            access = StoryAccess.SUBSCRIBER,
            scheduledPublishDateTime = DateUtils.addDays(Date(), 7),
        )

        val result = rest.postForEntity("/v1/stories/commands/publish", command, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertEquals(command.title, story.title)
        assertEquals(command.summary, story.summary)
        assertEquals(command.tagline, story.tagline)
        assertEquals(StoryStatus.DRAFT, story.status)
        assertEquals(command.topicId, story.topicId)
        assertEquals(command.access, story.access)
        assertTrue(story.publishedDateTime!!.before(now))
        assertTrue(story.modificationDateTime.after(now))
        assertEquals(
            DateUtils.beginingOfTheDay(command.scheduledPublishDateTime!!).time / 1000,
            DateUtils.beginingOfTheDay(story.scheduledPublishDateTime!!).time / 1000,
        )
        assertFalse(story.wpp)

        val tags = tagDao.findByNameIn(arrayListOf("covid-19", "test"))
        assertEquals(2, tags.size)

        val event = eventStore.events(
            streamId = StreamId.STORY,
            entityId = story.id.toString(),
            type = EventType.STORY_PUBLICATION_SCHEDULED_EVENT,
        ).last()
        assertEquals("1", event.userId)

        val payload = event.payload as StoryPublicationScheduledEventPayload
        assertEquals(command.access, payload.access)
        assertEquals(command.tagline, payload.tagline)
        assertEquals(command.summary, payload.summary)
        assertEquals(command.title, payload.title)
        assertEquals(command.tags, payload.tags)
        assertEquals(command.topicId, payload.topicId)
        assertEquals(
            DateUtils.beginingOfTheDay(command.scheduledPublishDateTime!!).time / 1000,
            DateUtils.beginingOfTheDay(payload.scheduledPublishDateTime).time / 1000,
        )
    }

    @Test
    fun `enable wpp`() {
        // GIVEN
        accessToken = "session-wpp"

        doReturn(
            WPPValidation(
                thumbnailRule = true,
                storyCountRule = true,
                readabilityRule = true,
                wordCountRule = true,
                subscriptionRule = true,
                blogAgeRule = true,
            )
        ).whenever(wppService).validate(any())

        // WHEN
        val command = PublishStoryCommand(
            storyId = 10L,
        )

        val result = rest.postForEntity("/v1/stories/commands/publish", command, Any::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val story = storyDao.findById(command.storyId).get()
        assertTrue(story.wpp)
    }

    @Test
    fun publishWithoutUpdating() {
        // GIVEN
        val now = Date()
        Thread.sleep(1000)

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
        assertNull(payload.summary)
        assertNull(payload.title)
        assertNull(payload.tags)
        assertNull(payload.topicId)
    }

    @Test
    fun error403() {
        // WHEN
        val command = PublishStoryCommand(
            storyId = 20L,
            title = "Schdule me",
            tagline = "This is awesome!",
            summary = "Summary of publish",
            topicId = 101L,
            tags = arrayListOf("COVID-19", "test"),
            access = StoryAccess.SUBSCRIBER,
            scheduledPublishDateTime = DateUtils.addDays(Date(), 7),
        )

        val result = rest.postForEntity("/v1/stories/commands/publish", command, Any::class.java)
        assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }
}
