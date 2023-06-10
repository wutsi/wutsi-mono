package com.wutsi.blog.kpi.it

import com.wutsi.blog.kpi.dao.KpiMonthlyRepository
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.job.KpiMonthlyImporterJob
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.user.dao.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.io.ByteArrayInputStream
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
    private lateinit var kpiDao: KpiMonthlyRepository

    @Autowired
    private lateinit var userDao: UserRepository

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

        assertKpiReadCount(100, now, 1)
        assertKpiReadCount(200, now, 20)
    }

    fun assertStoryReadCount(storyId: Long, value: Long) {
        assertEquals(value, storyDao.findById(storyId).get().readCount)
    }

    fun assertUserReadCount(userId: Long, value: Long) {
        assertEquals(value, userDao.findById(userId).get().readCount)
    }

    fun assertKpiReadCount(storyId: Long, now: LocalDate, value: Long) {
        val kpi = kpiDao.findByStoryIdAndTypeAndYearAndMonth(storyId, KpiType.READ, now.year, now.monthValue).get()
        assertEquals(value, kpi.value)
    }
}
