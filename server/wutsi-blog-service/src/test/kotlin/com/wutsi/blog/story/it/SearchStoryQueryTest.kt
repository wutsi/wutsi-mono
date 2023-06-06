package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.EventHandler
import com.wutsi.blog.story.dto.SearchStoryContext
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
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
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/story/SearchStoryQuery.sql"])
class SearchStoryQueryTest : ClientHttpRequestInterceptor {
    @Autowired
    lateinit var events: EventHandler

    @Autowired
    private lateinit var rest: TestRestTemplate

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

        doReturn("the-device-id").whenever(traceContext).deviceId()

        accessToken = null
        rest.restTemplate.interceptors = listOf(this)
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
}
