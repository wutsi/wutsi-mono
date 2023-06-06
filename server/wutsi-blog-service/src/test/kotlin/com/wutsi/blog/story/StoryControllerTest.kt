package com.wutsi.blog.story

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.EventHandler
import com.wutsi.blog.client.story.CountStoryResponse
import com.wutsi.blog.story.dao.StoryContentRepository
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dao.TagRepository
import com.wutsi.blog.story.dto.CreateStoryCommand
import com.wutsi.blog.story.dto.CreateStoryResponse
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.user.dto.GetStoryReadabilityResponse
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
