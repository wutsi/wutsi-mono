package com.wutsi.blog.story

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.EventHandler
import com.wutsi.blog.client.story.CountStoryResponse
import com.wutsi.blog.client.story.GetStoryReadabilityResponse
import com.wutsi.blog.story.dao.StoryContentRepository
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dao.TagRepository
import com.wutsi.blog.story.dto.CreateStoryCommand
import com.wutsi.blog.story.dto.CreateStoryResponse
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.SearchStoryContext
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.StoryAccess
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.tracing.TracingContext
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
import java.util.TimeZone
import kotlin.test.Ignore
import kotlin.test.assertEquals
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
        assertEquals(StoryStatus.PUBLISHED, story.status)

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
            status = StoryStatus.DRAFT,
            limit = 5,
            sortBy = StorySortStrategy.MODIFIED,
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
            status = StoryStatus.PUBLISHED,
            limit = 5,
            sortBy = StorySortStrategy.PUBLISHED,
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
            status = StoryStatus.PUBLISHED,
            limit = 5,
            sortBy = StorySortStrategy.PUBLISHED,
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
            status = StoryStatus.DRAFT,
            limit = 5,
            sortBy = StorySortStrategy.MODIFIED,
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
            status = StoryStatus.PUBLISHED,
            limit = 5,
            sortBy = StorySortStrategy.RECOMMENDED,
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
            status = StoryStatus.DRAFT,
            limit = Int.MAX_VALUE,
            offset = 0,
        )
        val result = rest.postForEntity("/v1/story/count", request, CountStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(7, result.body!!.total)
    }

    @Test
    fun readability() {
        val request = CreateStoryCommand(
            title = "Hello world",
            content = EDITORJS_CONTENT,
            userId = 1L,
        )
        val storyId =
            rest.postForEntity("/v1/stories/command/create", request, CreateStoryResponse::class.java).body!!.storyId

        val result = rest.getForEntity("/v1/story/$storyId/readability", GetStoryReadabilityResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)
        val readability = result.body!!.readability
        assertTrue(readability.score > 0)
        assertEquals(scoreThreshold.toInt(), readability.scoreThreshold)
        assertTrue(readability.rules.isNotEmpty())
    }
}
