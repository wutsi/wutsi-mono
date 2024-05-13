package com.wutsi.blog.ads.job

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.dao.AdsRepository
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.event.store.EventStore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.time.Clock
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ads/CompleteAdsJob.sql"])
class CompleteAdsJobTest {
    @Autowired
    private lateinit var job: CompleteAdsJob

    @Autowired
    private lateinit var dao: AdsRepository

    @Autowired
    private lateinit var eventStore: EventStore

    @MockBean
    private lateinit var clock: Clock

    private val now = SimpleDateFormat("yyyy-MM-dd").parse("2020-10-20")

    @BeforeEach
    fun setUp() {
        doReturn(now.time).whenever(clock).millis()
    }

    @Test
    fun run() {
        job.run()

        assertCompleted("100")
        assertNotCompleted("101")
        assertCompleted("102")
        assertCompleted("103")
        assertNotCompleted("104")
        assertNotCompleted("200")
        assertCompleted("201", false)
        assertCompleted("202", false)
    }

    fun assertCompleted(id: String, processed: Boolean = true) {
        val ads = dao.findById(id).get()
        assertEquals(AdsStatus.COMPLETED, ads.status)
        if (processed) {
            assertNotNull(ads.completedDateTime)
        }

        val events = eventStore.events(
            streamId = StreamId.ADS,
            entityId = id,
            type = EventType.ADS_COMPLETED_EVENT
        )
        if (processed) {
            assertEquals(1, events.size)
        } else {
            assertEquals(0, events.size)
        }
    }

    fun assertNotCompleted(id: String) {
        val ads = dao.findById(id).get()
        assertNotEquals(AdsStatus.COMPLETED, ads.status)
    }
}