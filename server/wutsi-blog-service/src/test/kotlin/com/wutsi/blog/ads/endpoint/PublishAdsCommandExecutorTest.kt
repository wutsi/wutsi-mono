package com.wutsi.blog.ads.endpoint

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.dao.AdsRepository
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.PublishAdsCommand
import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.time.Clock
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ads/PublishAdsCommand.sql"])
class PublishAdsCommandExecutorTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: AdsRepository

    @Autowired
    private lateinit var eventStore: EventStore

    @MockBean
    private lateinit var clock: Clock

    private val now = SimpleDateFormat("yyyy-MM-dd").parse("2024-12-20")

    @BeforeEach
    fun setUp() {
        doReturn(now.time).whenever(clock).millis()
    }

    @Test
    fun publish() {
        val request = PublishAdsCommand(id = "100")
        val response = rest.postForEntity("/v1/ads/commands/publish", request, Any::class.java)
        assertEquals(200, response.statusCode.value())

        val ads = dao.findById(request.id).get()
        assertEquals(AdsStatus.PUBLISHED, ads.status)
        assertNotNull(ads.publishedDateTime)

        val events = eventStore.events(
            streamId = StreamId.ADS,
            entityId = request.id,
            type = EventType.ADS_PUBLISHED_EVENT
        )
        assertEquals(1, events.size)
    }

    @Test
    fun `already published`() {
        val request = PublishAdsCommand(id = "900")
        val response = rest.postForEntity("/v1/ads/commands/publish", request, ErrorResponse::class.java)
        assertEquals(409, response.statusCode.value())

        assertEquals(ErrorCode.ADS_NOT_IN_DRAFT, response.body?.error?.code)
    }

    @Test
    fun `no image url`() {
        val request = PublishAdsCommand(id = "901")
        val response = rest.postForEntity("/v1/ads/commands/publish", request, ErrorResponse::class.java)
        assertEquals(409, response.statusCode.value())

        assertEquals(ErrorCode.ADS_IMAGE_URL_MISSING, response.body?.error?.code)
    }

    @Test
    fun `no url`() {
        val request = PublishAdsCommand(id = "902")
        val response = rest.postForEntity("/v1/ads/commands/publish", request, ErrorResponse::class.java)
        assertEquals(409, response.statusCode.value())

        assertEquals(ErrorCode.ADS_URL_MISSING, response.body?.error?.code)
    }

    @Test
    fun `no budget`() {
        val request = PublishAdsCommand(id = "903")
        val response = rest.postForEntity("/v1/ads/commands/publish", request, ErrorResponse::class.java)
        assertEquals(409, response.statusCode.value())

        assertEquals(ErrorCode.ADS_BUDGET_MISSING, response.body?.error?.code)
    }

    @Test
    fun `no end date`() {
        val request = PublishAdsCommand(id = "904")
        val response = rest.postForEntity("/v1/ads/commands/publish", request, ErrorResponse::class.java)
        assertEquals(409, response.statusCode.value())

        assertEquals(ErrorCode.ADS_END_DATE_MISSING, response.body?.error?.code)
    }

    @Test
    fun `start equals end date`() {
        val request = PublishAdsCommand(id = "905")
        val response = rest.postForEntity("/v1/ads/commands/publish", request, ErrorResponse::class.java)
        assertEquals(409, response.statusCode.value())

        assertEquals(ErrorCode.ADS_END_DATE_BEFORE_START_DATE, response.body?.error?.code)
    }

    @Test
    fun `start after end date`() {
        val request = PublishAdsCommand(id = "906")
        val response = rest.postForEntity("/v1/ads/commands/publish", request, ErrorResponse::class.java)
        assertEquals(409, response.statusCode.value())

        assertEquals(ErrorCode.ADS_END_DATE_BEFORE_START_DATE, response.body?.error?.code)
    }

    @Test
    fun `no start date`() {
        val request = PublishAdsCommand(id = "907")
        val response = rest.postForEntity("/v1/ads/commands/publish", request, ErrorResponse::class.java)
        assertEquals(409, response.statusCode.value())

        assertEquals(ErrorCode.ADS_START_DATE_MISSING, response.body?.error?.code)
    }
}