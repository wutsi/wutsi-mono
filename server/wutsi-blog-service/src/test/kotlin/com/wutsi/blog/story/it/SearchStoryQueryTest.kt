package com.wutsi.blog.story.it

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SortOrder
import com.wutsi.blog.story.dao.ViewRepository
import com.wutsi.blog.story.domain.ViewEntity
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
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var viewDao: ViewRepository

    @MockBean
    private lateinit var traceContext: TracingContext

    private var accessToken: String? = null
    private val deviceId = "the-device-id"

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
        doReturn(deviceId).whenever(traceContext).deviceId()

        accessToken = null
        rest.restTemplate.interceptors = listOf(this)
    }

    @Test
    fun draft() {
        val request = SearchStoryRequest(
            userIds = listOf(2L),
            status = StoryStatus.DRAFT,
            limit = 5,
            sortBy = StorySortStrategy.MODIFIED,
        )
        val result = rest.postForEntity("/v1/stories/queries/search", request, SearchStoryResponse::class.java)

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
    fun `bubble down viewed stories`() {
        // GIVEN
        viewDao.save(ViewEntity(null, deviceId, 10))
        viewDao.save(ViewEntity(null, deviceId, 12))

        // WHEN
        val request = SearchStoryRequest(
            userIds = listOf(2L),
            status = StoryStatus.DRAFT,
            limit = 5,
            sortBy = StorySortStrategy.MODIFIED,
            bubbleDownViewedStories = true,
        )
        val result = rest.postForEntity("/v1/stories/queries/search", request, SearchStoryResponse::class.java)

        // THEN
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
    fun published() {
        val request = SearchStoryRequest(
            userIds = listOf(2L),
            status = StoryStatus.PUBLISHED,
            limit = 5,
            sortBy = StorySortStrategy.PUBLISHED,
        )
        val result = rest.postForEntity("/v1/stories/queries/search", request, SearchStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val stories = result.body!!.stories
        assertEquals(2, stories.size)
        assertEquals(18L, stories[0].id)
        assertEquals(11L, stories[1].id)
    }

    @Test
    fun `active users`() {
        val request = SearchStoryRequest(
            userIds = listOf(2L, 10L),
            limit = 5,
            activeUserOnly = true,
        )
        val result = rest.postForEntity("/v1/stories/queries/search", request, SearchStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val stories = result.body!!.stories
        assertEquals(2, stories.size)
        assertEquals(24L, stories[0].id)
        assertEquals(25L, stories[1].id)
    }

    @Test
    fun dedup() {
        val request = SearchStoryRequest(
            status = StoryStatus.DRAFT,
            limit = 5,
            dedupUser = true,
        )
        val result = rest.postForEntity("/v1/stories/queries/search", request, SearchStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val stories = result.body!!.stories
        assertEquals(3, stories.size)
        assertEquals(1L, stories[0].id)
        assertEquals(10L, stories[1].id)
        assertEquals(24L, stories[2].id)
    }

    @Test
    fun `by tags`() {
        val request = SearchStoryRequest(
            tags = listOf("Covid 19", "gitflow"),
        )
        val result = rest.postForEntity("/v1/stories/queries/search", request, SearchStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val stories = result.body!!.stories.sortedBy { it.id }
        assertEquals(2, stories.size)
        assertEquals(1L, stories[0].id)
        assertEquals(2L, stories[1].id)
    }

    @Test
    fun `recommended sort`() {
        val request = SearchStoryRequest(
            userIds = listOf(2L),
            status = StoryStatus.PUBLISHED,
            limit = 5,
            sortBy = StorySortStrategy.RECOMMENDED,
        )
        val result = rest.postForEntity("/v1/stories/queries/search", request, SearchStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val stories = result.body!!.stories
        assertEquals(2, stories.size)
        assertEquals(11L, stories[1].id)
        assertEquals(18L, stories[0].id)
    }

    @Test
    fun `published by popularity`() {
        val request = SearchStoryRequest(
            userIds = listOf(2L),
            status = StoryStatus.PUBLISHED,
            limit = 5,
            sortBy = StorySortStrategy.POPULARITY,
            sortOrder = SortOrder.DESCENDING,
        )
        val result = rest.postForEntity("/v1/stories/queries/search", request, SearchStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val stories = result.body!!.stories
        assertEquals(2, stories.size)
        assertEquals(11L, stories[0].id)
        assertEquals(18L, stories[1].id)
    }

    @Test
    fun `no sort`() {
        val request = SearchStoryRequest(
            userIds = listOf(2L),
            status = StoryStatus.DRAFT,
            limit = 5,
            sortBy = StorySortStrategy.NONE,
            storyIds = listOf(15L, 14, 13, 12, 10)
        )
        val result = rest.postForEntity("/v1/stories/queries/search", request, SearchStoryResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val stories = result.body!!.stories
        assertEquals(5, stories.size)
        assertEquals(15L, stories[0].id)
        assertEquals(14L, stories[1].id)
        assertEquals(13L, stories[2].id)
        assertEquals(12L, stories[3].id)
        assertEquals(10L, stories[4].id)
    }
}
