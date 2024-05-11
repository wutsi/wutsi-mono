package com.wutsi.blog.ads.job

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.dao.AdsRepository
import com.wutsi.blog.ads.dto.AdsStatus
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

        assertRunning("100")

        assertNotRunning("101")
        assertNotRunning("102")
        assertNotRunning("103")
    }

    fun assertRunning(id: String) {
        val ads = dao.findById(id).get()
        assertEquals(AdsStatus.RUNNING, ads.status)
    }

    fun assertNotRunning(id: String) {
        val ads = dao.findById(id).get()
        assertNotEquals(AdsStatus.RUNNING, ads.status)
    }
}