package com.wutsi.blog.ads.job

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.dao.AdsRepository
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.event.store.EventStore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.time.Clock

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ads/RunAdsJob.sql"])
class RunAdsJobTest {
    @Autowired
    private lateinit var job: RunAdsJob

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

        val ads = dao.findById("100").get()
        assertEquals(AdsStatus.RUNNING, ads.status)
        assertEquals(1000, ads.maxImpressions)
        assertEquals(200, ads.maxDailyImpressions)

        assertNotRunning("101")
        assertNotRunning("102")
    }

    fun assertNotRunning(id: String) {
        val ads = dao.findById(id).get()
        assertNotEquals(AdsStatus.RUNNING, ads.status)
    }
}