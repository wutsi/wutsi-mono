package com.wutsi.blog.story

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.EventHandler
import com.wutsi.blog.ResourceHelper.loadResourceAsString
import com.wutsi.blog.client.story.CountStoryResponse
import com.wutsi.blog.client.story.GetStoryReadabilityResponse
import com.wutsi.blog.client.story.GetStoryResponse
import com.wutsi.blog.client.story.PublishStoryRequest
import com.wutsi.blog.client.story.PublishStoryResponse
import com.wutsi.blog.client.story.SaveStoryRequest
import com.wutsi.blog.client.story.SaveStoryResponse
import com.wutsi.blog.client.story.SearchStoryContext
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SearchStoryResponse
import com.wutsi.blog.client.story.StoryAccess
import com.wutsi.blog.client.story.StoryAccess.SUBSCRIBER
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.story.WPPStatus
import com.wutsi.blog.story.dao.StoryContentRepository
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dao.TagRepository
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.tracing.TracingContext
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.Date
import java.util.TimeZone
import kotlin.test.Ignore
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/StoryController.sql"])
class StoryControllerTest {
    companion object {
        const val EDITORJS_CONTENT = "{" +
            "\"time\":1584718404278," +
            "\"blocks\":[" +
            "{\"type\":\"paragraph\",\"data\":{\"text\":\"Hello world\"}}," +
            "{\"type\":\"paragraph\",\"data\":{\"text\":\"This is second line\"}}" +
            "]," +
            "\"version\":\"2.17.0\"}"
    }

    @Autowired
    lateinit var events: EventHandler

    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var storyDao: StoryRepository

    @Autowired
    private lateinit var contentDao: StoryContentRepository

    @Autowired
    private lateinit var clock: Clock

    @Autowired
    private lateinit var tagDao: TagRepository

    @Autowired
    private lateinit var cacheManager: CacheManager

    @Value("\${wutsi.readability.score-threshold}")
    private lateinit var scoreThreshold: Integer

    @LocalServerPort
    private lateinit var port: Integer

    private lateinit var fmt: SimpleDateFormat

    @MockBean
    private lateinit var traceContext: TracingContext

    @MockBean
    private lateinit var cache: Cache

    @BeforeEach
    fun setUp() {
        events.init()

        fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        doReturn("the-device-id").whenever(traceContext).deviceId()
    }

    @Test
    fun create() {
        val json = loadResourceAsString("/editorjs.json")
        val request = SaveStoryRequest(
            accessToken = "session-ray",
            title = "Hello world",
            content = json,
            contentType = "application/editorjs",
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/story", request, SaveStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val id = result.body!!.storyId
        val story = storyDao.findById(id).get()
        assertEquals(request.title, story.title)
        assertTrue(story.wordCount > 0)
        assertEquals(
            "Hey. Meet the new Editor. On this page you can see it in action — try to edit this text",
            story.summary,
        )
        assertEquals(1, story.readingMinutes)
        assertEquals("en", story.language)
        assertEquals(StoryStatus.draft, story.status)
        assertEquals("/upload/temporary/o_488cfb382712d6af914301c73f376e8c.jpg", story.thumbnailUrl)
        assertNull(story.sourceUrl)
        assertNotNull(story.creationDateTime)
        assertNotNull(story.modificationDateTime)
        assertNull(story.publishedDateTime)
        assertTrue(story.readabilityScore > 0)
        assertNull(story.topicId)
        assertFalse(story.deleted)
        assertNull(story.deletedDateTime)
        assertEquals(request.siteId, story.siteId)

        val content = contentDao.findByStory(story)[0]
        assertEquals(story.title, content.title)
        assertEquals(story.language, content.language)
        assertEquals(story.summary, content.summary)
        assertEquals(story.tagline, content.tagline)
        assertEquals(request.content, content.content)
        assertEquals(request.contentType, content.contentType)
        assertNotNull(story.creationDateTime)
        assertNotNull(story.modificationDateTime)
    }

    @Test
    fun createRunAs() {
        val request = SaveStoryRequest(
            accessToken = "session-ze",
            title = "Hello world",
            contentType = "application/editorjs",
            content = null,
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/story", request, SaveStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val id = result.body!!.storyId

        val story = storyDao.findById(id).get()
        assertEquals(1L, story.userId)
        assertEquals(request.siteId, story.siteId)
    }

    @Test
    fun createNoContent() {
        val request = SaveStoryRequest(
            accessToken = "session-ray",
            title = "How to create healthy breakfast with 5 veggies",
            content = null,
            contentType = "application/editorjs",
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/story", request, SaveStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val id = result.body!!.storyId
        val story = storyDao.findById(id).get()
        assertEquals(request.title, story.title)
        assertEquals("", story.summary)
        assertEquals(0, story.readingMinutes)
        assertFalse(story.language.isNullOrEmpty())
        assertEquals("", story.thumbnailUrl)
        assertNotNull(story.creationDateTime)
        assertNotNull(story.modificationDateTime)
        assertEquals(0, story.readabilityScore)
        assertNull(story.topicId)
        assertEquals(request.siteId, story.siteId)

        val content = contentDao.findByStory(story)[0]
        assertEquals(story.title, content.title)
        assertEquals(story.language, content.language)
        assertEquals(story.summary, content.summary)
        assertEquals(story.tagline, content.tagline)
        assertNull(content.content)
        assertNotNull(story.creationDateTime)
        assertNotNull(story.modificationDateTime)
    }

    @Test
    fun createInvalidAccessToken() {
        val request = SaveStoryRequest(
            accessToken = "xxxx",
            title = "Hello world",
            contentType = "application/editorjs",
            content = EDITORJS_CONTENT,
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/story", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }

    @Test
    fun update() {
        val now = clock.millis()
        Thread.sleep(1000)

        val json = loadResourceAsString("/editorjs.json")
        val request = SaveStoryRequest(
            accessToken = "session-ray",
            title = "Hello world",
            contentType = "application/editorjs",
            content = json,
            siteId = 13L,
        )
        val result = rest.postForEntity("/v1/story/1", request, SaveStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val id = result.body!!.storyId
        assertEquals(1L, result.body!!.storyId)

        val story = storyDao.findById(id).get()
        assertEquals(request.title, story.title)
        assertTrue(story.wordCount > 0)
        assertEquals(
            "This is the toolkit library from which all other modules inherit functionality. It also includes the core facades for the Tika API.",
            story.summary,
        )
        assertEquals(1, story.readingMinutes)
        assertEquals("en", story.language)
        assertEquals("/upload/temporary/o_488cfb382712d6af914301c73f376e8c.jpg", story.thumbnailUrl)
        assertTrue(story.modificationDateTime.time > now)
        assertTrue(story.readabilityScore > 0)
        assertFalse(story.deleted)
        assertNull(story.deletedDateTime)
        assertEquals(1L, story.siteId)

        val content = contentDao.findByStory(story)[0]
        assertEquals(story.title, content.title)
        assertEquals(story.language, content.language)
        assertEquals(story.summary, content.summary)
        assertEquals(story.tagline, content.tagline)
        assertEquals(request.content, content.content)
        assertTrue(content.modificationDateTime.time > now)
    }

    @Test
    fun updateSuperUser() {
        val now = clock.millis()
        Thread.sleep(1000)

        val request = SaveStoryRequest(
            accessToken = "session-ze",
            title = "Hello world",
            contentType = "application/editorjs",
            content = null,
            siteId = 13L,
        )
        val result = rest.postForEntity("/v1/story/1", request, SaveStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val id = result.body!!.storyId
        assertEquals(1L, result.body!!.storyId)

        val story = storyDao.findById(id).get()
        assertTrue(story.modificationDateTime.time > now)
        assertEquals(1L, story.siteId)

        val content = contentDao.findByStoryAndLanguage(story, story.language).get()
        assertTrue(content.modificationDateTime.time > now)
    }

    @Test
    fun updateNoContent() {
        val now = clock.millis()
        Thread.sleep(1000)

        val request = SaveStoryRequest(
            accessToken = "session-ray",
            title = "Hello world",
            contentType = "application/editorjs",
            content = null,
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/story/1", request, SaveStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val id = result.body!!.storyId
        assertEquals(1L, result.body!!.storyId)

        val story = storyDao.findById(id).get()
        assertEquals(request.title, story.title)
        assertTrue(story.modificationDateTime.time > now)
        assertEquals(0, story.readabilityScore)
        assertEquals(0, story.wordCount)
        assertNull(story.topicId)
        assertFalse(story.deleted)
        assertNull(story.deletedDateTime)
        assertEquals(1L, story.siteId)

        val content = contentDao.findByStoryAndLanguage(story, story.language).get()
        assertEquals(story.title, content.title)
        assertEquals(story.language, content.language)
        assertEquals(story.summary, content.summary)
        assertEquals(story.tagline, content.tagline)
        assertNull(content.content)
        assertTrue(story.modificationDateTime.time > now)
    }

    @Test
    fun updateInvalidAccessToken() {
        val request = SaveStoryRequest(
            accessToken = "xxxx",
            title = "Hello world",
            contentType = "application/editorjs",
            content = EDITORJS_CONTENT,
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/story/1", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
        assertEquals("session_not_found", result.body!!.error.code)
    }

    @Test
    fun updatePermissionDenied() {
        val request = SaveStoryRequest(
            accessToken = "session-john",
            title = "Hello world",
            contentType = "application/editorjs",
            content = EDITORJS_CONTENT,
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/story/1", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
        assertEquals("permission_denied", result.body!!.error.code)
    }

    @Test
    fun updateStoryNotFound() {
        val request = SaveStoryRequest(
            accessToken = "session-ray",
            title = "Hello world",
            contentType = "application/editorjs",
            content = EDITORJS_CONTENT,
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/story/9999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals("story_not_found", result.body!!.error.code)
    }

    @Test
    fun updateDeletedStory() {
        val request = SaveStoryRequest(
            accessToken = "session-ray",
            title = "Hello world",
            contentType = "application/editorjs",
            content = EDITORJS_CONTENT,
            siteId = 11L,
        )
        val result = rest.postForEntity("/v1/story/99", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals("story_not_found", result.body!!.error.code)
    }

    @Test
    fun get() {
        val result = rest.getForEntity("/v1/story/2", GetStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val story = result.body!!.story
        assertEquals("Sample Story", story.title)
        assertEquals("Sample Tagline", story.tagline)
        assertEquals("text/plain", story.contentType)
        assertEquals("World", story.content)
        assertEquals("en", story.language)
        assertEquals("http://www.img.com/goo.png", story.thumbnailUrl)
        assertEquals("http://www.test.com/1/1/test.txt", story.sourceUrl)
        assertEquals(7, story.readingMinutes)
        assertEquals(1430, story.wordCount)
        assertEquals("/read/2/sample-story", story.slug)
        assertEquals(2, story.readabilityScore)
        assertEquals(StoryStatus.published, story.status)
        assertFalse(story.live)
        assertNull(story.liveDateTime)
        assertEquals(WPPStatus.rejected, story.wppStatus)
        assertEquals("offensive", story.wppRejectionReason)
        assertNotNull(story.wppModificationDateTime)
        assertEquals(1L, story.siteId)

        assertEquals(2, story.tags.size)

        assertEquals(1, story.tags[0].id)
        assertEquals("covid-19", story.tags[0].name)
        assertEquals("COVID-19", story.tags[0].displayName)
        assertEquals(100L, story.tags[0].totalStories)

        assertEquals(4, story.tags[1].id)
        assertEquals("gitflow", story.tags[1].name)
        assertEquals("GitFlow", story.tags[1].displayName)
        assertEquals(7L, story.tags[1].totalStories)

        assertEquals(101L, story.topic?.id)
        assertEquals("art", story.topic?.name)

        assertEquals("2040-01-02", fmt.format(story.scheduledPublishDateTime))
        assertEquals(StoryAccess.PREMIUM_SUBSCRIBER, story.access)
    }

    @Test
    fun getNotFound() {
        val result = rest.getForEntity("/v1/story/9999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals("story_not_found", result.body!!.error.code)
    }

    @Test
    fun getDeleted() {
        val result = rest.getForEntity("/v1/story/99", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals("story_not_found", result.body!!.error.code)
    }

    @Test
    fun searchDraft() {
        val request = SearchStoryRequest(
            userIds = listOf(2L),
            status = StoryStatus.draft,
            limit = 5,
            sortBy = StorySortStrategy.modified,
        )
        val result = rest.postForEntity("/v1/story/search", request, SearchStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val stories = result.body!!.stories
        assertEquals(5, stories.size)
        assertEquals(10L, stories[0].id)
        assertEquals(12L, stories[1].id)
        assertEquals(13L, stories[2].id)
        assertEquals(14L, stories[3].id)
        assertEquals(15L, stories[4].id)
    }

    @Test
    fun searchPublished() {
        val request = SearchStoryRequest(
            userIds = listOf(2L),
            status = StoryStatus.published,
            live = true,
            limit = 5,
            sortBy = StorySortStrategy.published,
        )
        val result = rest.postForEntity("/v1/story/search", request, SearchStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val stories = result.body!!.stories
        assertEquals(2, stories.size)
        assertEquals(18L, stories[0].id)
        assertEquals(11L, stories[1].id)
    }

    @Test
    fun searchDedupUser() {
        val request = SearchStoryRequest(
            userIds = listOf(2L),
            status = StoryStatus.published,
            live = true,
            limit = 5,
            sortBy = StorySortStrategy.published,
        )
        val result = rest.postForEntity("/v1/story/search", request, SearchStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val stories = result.body!!.stories
        val userIds = stories.map { it.userId }
        assertEquals(stories.size, userIds.size)
    }

    @Test
    fun searchByTag() {
        val request = SearchStoryRequest(
            tags = listOf("Covid 19", "gitflow"),
        )
        val result = rest.postForEntity("/v1/story/search", request, SearchStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val stories = result.body!!.stories.sortedBy { it.id }
        assertEquals(2, stories.size)
        assertEquals(1L, stories[0].id)
        assertEquals(2L, stories[1].id)
    }

    @Test
    @Ignore
    fun searchBubbleDownUser() {
        cache.put(10L, mutableSetOf("1", "2", "3"))
        cache.put(12L, mutableSetOf("1", "2"))

        val request = SearchStoryRequest(
            userIds = listOf(2L),
            status = StoryStatus.draft,
            limit = 5,
            sortBy = StorySortStrategy.modified,
            context = SearchStoryContext(deviceId = "1"),
        )
        val result = rest.postForEntity("/v1/story/search", request, SearchStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val stories = result.body!!.stories
        assertEquals(5, stories.size)
        assertEquals(13L, stories[0].id)
        assertEquals(14L, stories[1].id)
        assertEquals(15L, stories[2].id)
        assertEquals(10L, stories[3].id)
        assertEquals(12L, stories[4].id)
    }

    @Test
    fun searchPublishedWithRecommendedSort() {
        val request = SearchStoryRequest(
            userIds = listOf(2L),
            status = StoryStatus.published,
            live = true,
            limit = 5,
            sortBy = StorySortStrategy.recommended,
            context = SearchStoryContext(
                userId = 11,
                deviceType = "222",
                deviceId = "333",
                traffic = "4444",
                language = "fr",
            ),
        )
        val result = rest.postForEntity("/v1/story/search", request, SearchStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val stories = result.body!!.stories
        assertEquals(2, stories.size)
        assertEquals(11L, stories[1].id)
        assertEquals(18L, stories[0].id)
    }

    @Test
    fun count() {
        val request = SearchStoryRequest(
            userIds = listOf(2L),
            status = StoryStatus.draft,
            limit = Int.MAX_VALUE,
            offset = 0,
        )
        val result = rest.postForEntity("/v1/story/count", request, CountStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(7, result.body!!.total)
    }

    @Test
    fun readability() {
        val request = SaveStoryRequest(
            accessToken = "session-ray",
            title = "Hello world",
            contentType = "application/editorjs",
            content = EDITORJS_CONTENT,
            siteId = 1L,
        )
        val storyId = rest.postForEntity("/v1/story", request, SaveStoryResponse::class.java).body!!.storyId

        val result = rest.getForEntity("/v1/story/$storyId/readability", GetStoryReadabilityResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)
        val readability = result.body!!.readability
        assertTrue(readability.score > 0)
        assertEquals(scoreThreshold.toInt(), readability.scoreThreshold)
        assertTrue(readability.rules.isNotEmpty())
    }

    @Test
    fun publish() {
        val now = Date(clock.millis())
        Thread.sleep(1000)

        val request = PublishStoryRequest(
            title = "Publish me",
            tagline = "This is awesome!",
            summary = "Summary of publish",
            topidId = 101L,
            tags = arrayListOf("COVID-19", "test"),
            publishToSocialMedia = true,
            socialMediaMessage = "This is the twitter message. #wutsi #loveit",
            access = SUBSCRIBER,
        )

        val result = rest.postForEntity("/v1/story/20/publish", request, PublishStoryResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val storyId = result.body!!.storyId
        assertEquals(20L, storyId)

        val story = storyDao.findById(storyId).get()
        assertEquals(request.title, story.title)
        assertEquals(request.summary, story.summary)
        assertEquals(request.tagline, story.tagline)
        assertEquals(StoryStatus.published, story.status)
        assertTrue(story.live)
        assertEquals(true, story.publishedDateTime?.after(now))
        assertEquals(story.publishedDateTime, story.modificationDateTime)
        assertEquals(story.publishedDateTime, story.liveDateTime)
        assertEquals(request.topidId, story.topicId)
        assertNull(story.wppStatus)
        assertNull(story.wppRejectionReason)
        assertNull(story.wppModificationDateTime)
        assertNull(story.scheduledPublishDateTime)
        assertEquals(request.publishToSocialMedia, story.publishToSocialMedia)
        assertEquals(request.socialMediaMessage, story.socialMediaMessage)
        assertEquals(request.access, story.access)

        val tags = tagDao.findByNameIn(arrayListOf("covid-19", "test"))
        assertEquals(2, tags.size)

        assertNotNull(events.publishEvent)
        assertEquals(storyId, events.publishEvent?.storyId)
    }

    @Test
    fun republish() {
        val now = Date(clock.millis())
        Thread.sleep(1000)

        val request = PublishStoryRequest(
            title = "Publish me",
            summary = "Summary of publish",
            tagline = "This is awesome!",
            topidId = 103,
            tags = arrayListOf("COVID-19", "test", "Computer"),
        )

        val result = rest.postForEntity("/v1/story/21/publish", request, PublishStoryResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val storyId = result.body!!.storyId
        assertEquals(21L, storyId)

        val story = storyDao.findById(storyId).get()
        assertEquals(request.title, story.title)
        assertEquals(request.summary, story.summary)
        assertEquals(request.tagline, story.tagline)
        assertEquals(StoryStatus.published, story.status)
        assertEquals(request.topidId, story.topicId)
        assertEquals(true, story.publishedDateTime?.before(now))
        assertTrue(story.modificationDateTime.after(now))
        assertNull(story.scheduledPublishDateTime)

        val tags = tagDao.findByNameIn(arrayListOf("COVID-19", "Test", "Computer"))
        assertEquals(3, tags.size)

        assertNull(events.publishEvent)
    }

    @Test
    fun schedulePublish() {
        val request = PublishStoryRequest(
            title = "Schedule my publishing",
            summary = "Summary of publish",
            topidId = 101L,
            tags = arrayListOf("COVID-19", "test"),
            tagline = "This is a nice tagline",
            scheduledPublishDateTime = DateUtils.addMonths(Date(), 10),
            publishToSocialMedia = true,
            socialMediaMessage = "This is the twitter message. #wutsi #loveit",
        )

        val result = rest.postForEntity("/v1/story/25/publish", request, PublishStoryResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)

        val storyId = result.body!!.storyId
        assertEquals(25L, storyId)

        val story = storyDao.findById(storyId).get()
        assertEquals(request.title, story.title)
        assertEquals(request.summary, story.summary)
        assertEquals(request.tagline, story.tagline)
        assertEquals(request.topidId, story.topicId)
        assertEquals(StoryStatus.draft, story.status)
        assertNull(story.publishedDateTime)
        assertEquals(request.publishToSocialMedia, story.publishToSocialMedia)
        assertEquals(request.socialMediaMessage, story.socialMediaMessage)

        assertEquals(fmt.format(request.scheduledPublishDateTime), fmt.format(story.scheduledPublishDateTime))

        assertNull(events.publishEvent)
    }

    @Test
    fun publishNoTile() {
        val request = PublishStoryRequest(
            summary = "No title",
            topidId = 101L,
            tags = arrayListOf("COVID-19", "test", "Computer"),
        )

        val result = rest.postForEntity("/v1/story/22/publish", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun publishNoSummary() {
        val request = PublishStoryRequest(
            title = "No Summary",
            topidId = 201L,
            tags = arrayListOf("COVID-19", "test", "Computer"),
        )

        val result = rest.postForEntity("/v1/story/23/publish", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun schedulePublishInThePast() {
        val request = PublishStoryRequest(
            title = "No Summary",
            summary = "Sample summary",
            topidId = 201L,
            tags = arrayListOf("COVID-19", "test", "Computer"),
            scheduledPublishDateTime = DateUtils.addDays(Date(), -10),
        )

        val result = rest.postForEntity("/v1/story/23/publish", request, ErrorResponse::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun delete() {
        rest.delete("/v1/story/90")

        val story = storyDao.findById(90L).get()
        assertTrue(story.deleted)
        assertNotNull(story.deletedDateTime)
    }
}
