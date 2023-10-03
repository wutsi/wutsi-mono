package com.wutsi.blog.kpi.it

import com.wutsi.blog.kpi.dao.StoryKpiRepository
import com.wutsi.blog.kpi.dao.UserKpiRepository
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.job.KpiMonthlyImporterJob
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.user.dao.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.io.ByteArrayInputStream
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/kpi/KpiMonthlyImporter.sql"])
internal class KpiMonthlyImporterTest {
    @Autowired
    private lateinit var job: KpiMonthlyImporterJob

    @Autowired
    private lateinit var storage: TrackingStorageService

    @Autowired
    private lateinit var storyDao: StoryRepository

    @Autowired
    private lateinit var storyKpiDao: StoryKpiRepository

    @Autowired
    private lateinit var userKpiDao: UserKpiRepository

    @Autowired
    private lateinit var userDao: UserRepository

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @BeforeEach
    fun setUp() {
        File(storageDir).deleteRecursively()
    }

    @Test
    fun run() {
        // GIVEN
        val now = LocalDate.now()
        storage.store(
            "kpi/monthly/" + now.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv",
            ByteArrayInputStream(
                """
                    product_id, total_reads
                    100,1
                    200,20
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        // WHEN
        job.run()

        // THEN
        assertStoryReadCount(100, 1)
        assertStoryReadCount(101, 11)
        assertStoryReadCount(200, 31)

        assertUserReadCount(111, 12)
        assertUserReadCount(211, 31)

        assertStoryKpiReadCount(100, now, 1)
        assertStoryKpiReadCount(200, now, 20)

        assertUserKpiReadCount(111, now, 1)
        assertUserKpiReadCount(211, now, 20)

        assertUserKpiSubscriptionCount(111, now, 2)
        assertUserKpiSubscriptionCount(211, now, 1)
    }

    fun assertStoryReadCount(storyId: Long, value: Long) {
        assertEquals(value, storyDao.findById(storyId).get().readCount)
    }

    fun assertUserReadCount(userId: Long, value: Long) {
        assertEquals(value, userDao.findById(userId).get().readCount)
    }

    fun assertStoryKpiReadCount(storyId: Long, now: LocalDate, value: Long) {
        val kpi =
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                storyId,
                KpiType.READ,
                now.year,
                now.monthValue,
                TrafficSource.ALL,
            ).get()
        assertEquals(value, kpi.value)
    }

    fun assertUserKpiReadCount(userId: Long, now: LocalDate, value: Long) {
        val kpi =
            userKpiDao.findByUserIdAndTypeAndYearAndMonthAndSource(
                userId,
                KpiType.READ,
                now.year,
                now.monthValue,
                TrafficSource.ALL,
            ).get()
        assertEquals(value, kpi.value)
    }

    fun assertUserKpiSubscriptionCount(userId: Long, now: LocalDate, value: Long) {
        val kpi =
            userKpiDao.findByUserIdAndTypeAndYearAndMonthAndSource(
                userId,
                KpiType.SUBSCRIPTION,
                now.year,
                now.monthValue,
                TrafficSource.ALL,
            ).get()
        assertEquals(value, kpi.value)
    }
}
