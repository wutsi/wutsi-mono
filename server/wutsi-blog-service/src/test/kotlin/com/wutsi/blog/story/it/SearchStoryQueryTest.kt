package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.EventHandler
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.StoryAccess
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.tracing.TracingContext
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
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/story/GetStoryQuery.sql"])
class GetStoryQueryTest : ClientHttpRequestInterceptor {
    @Autowired
    lateinit var events: EventHandler

    @Autowired
    private lateinit var rest: TestRestTemplate

    private lateinit var fmt: SimpleDateFormat

    @MockBean
    private lateinit var traceContext: TracingContext

    private var accessToken: String? = null

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
        events.init()

        fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        doReturn("the-device-id").whenever(traceContext).deviceId()

        accessToken = null
        rest.restTemplate.interceptors = listOf(this)
    }

    @Test
    fun anonymous() {
        val result = rest.getForEntity("/v1/stories/10", GetStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val story = result.body!!.story
        assertEquals("Sample Story", story.title)
        assertEquals("Sample Tagline", story.tagline)
        assertEquals("application/editorjs", story.contentType)
        assertEquals("{\"time\":1584718404278, \"blocks\":[]}", story.content)
        assertEquals("en", story.language)
        assertEquals("https://www.img.com/goo.png", story.thumbnailUrl)
        assertEquals("https://www.test.com/1/1/test.txt", story.sourceUrl)
        assertEquals(7, story.readingMinutes)
        assertEquals(1430, story.wordCount)
        assertEquals("/read/10/sample-story", story.slug)
        assertEquals(2, story.readabilityScore)
        assertEquals(StoryStatus.PUBLISHED, story.status)
        assertEquals("2018-01-30", fmt.format(story.publishedDateTime))
        assertEquals(StoryAccess.PUBLIC, story.access)
        assertEquals(0, story.totalComments)
        assertEquals(0, story.totalLikes)
        assertEquals(0, story.totalShares)
        assertFalse(story.commented)
        assertFalse(story.liked)
        assertFalse(story.pinned)

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
    }

    @Test
    fun withCounters() {
        val result = rest.getForEntity("/v1/stories/20", GetStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val story = result.body!!.story
        assertEquals("Sample Story", story.title)
        assertEquals("Sample Tagline", story.tagline)
        assertEquals("application/editorjs", story.contentType)
        assertEquals("{\"time\":1584718404278, \"blocks\":[]}", story.content)
        assertEquals("en", story.language)
        assertEquals("https://www.img.com/goo.png", story.thumbnailUrl)
        assertEquals("https://www.test.com/1/1/test.txt", story.sourceUrl)
        assertEquals(7, story.readingMinutes)
        assertEquals(1430, story.wordCount)
        assertEquals("/read/20/sample-story", story.slug)
        assertEquals(2, story.readabilityScore)
        assertEquals(StoryStatus.PUBLISHED, story.status)
        assertEquals("2018-01-30", fmt.format(story.publishedDateTime))
        assertEquals(StoryAccess.PUBLIC, story.access)
        assertEquals(22, story.totalComments)
        assertEquals(11, story.totalLikes)
        assertEquals(33, story.totalShares)
        assertFalse(story.commented)
        assertFalse(story.liked)
        assertTrue(story.pinned)

        assertEquals(0, story.tags.size)

        assertEquals(101L, story.topic?.id)
        assertEquals("art", story.topic?.name)
    }

    @Test
    fun loggedIn() {
        accessToken = "session-ray"

        val result = rest.getForEntity("/v1/stories/20", GetStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)
        val story = result.body!!.story
        assertEquals("Sample Story", story.title)
        assertEquals("Sample Tagline", story.tagline)
        assertEquals("application/editorjs", story.contentType)
        assertEquals("{\"time\":1584718404278, \"blocks\":[]}", story.content)
        assertEquals("en", story.language)
        assertEquals("https://www.img.com/goo.png", story.thumbnailUrl)
        assertEquals("https://www.test.com/1/1/test.txt", story.sourceUrl)
        assertEquals(7, story.readingMinutes)
        assertEquals(1430, story.wordCount)
        assertEquals("/read/20/sample-story", story.slug)
        assertEquals(2, story.readabilityScore)
        assertEquals(StoryStatus.PUBLISHED, story.status)
        assertEquals("2018-01-30", fmt.format(story.publishedDateTime))
        assertEquals(StoryAccess.PUBLIC, story.access)
        assertEquals(22, story.totalComments)
        assertEquals(11, story.totalLikes)
        assertEquals(33, story.totalShares)
        assertTrue(story.commented)
        assertTrue(story.liked)
        assertTrue(story.pinned)

        assertEquals(0, story.tags.size)

        assertEquals(101L, story.topic?.id)
        assertEquals("art", story.topic?.name)
    }

    @Test
    fun notFound() {
        val result = rest.getForEntity("/v1/stories/9999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals("story_not_found", result.body!!.error.code)
    }

    @Test
    fun deleted() {
        val result = rest.getForEntity("/v1/stories/99", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals("story_not_found", result.body!!.error.code)
    }
}
