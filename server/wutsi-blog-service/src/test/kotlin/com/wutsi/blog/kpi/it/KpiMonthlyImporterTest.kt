package com.wutsi.blog.kpi.it

import com.wutsi.blog.kpi.dao.KpiMonthlyRepository
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.job.KpiMonthlyImporterJob
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.dao.StoryRepository
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
        val story100 = storyDao.findById(100).get()
        assertEquals(1, story100.readCount)

        val kpi100 =
            kpiDao.findByStoryIdAndTypeAndYearAndMonth(story100.id!!, KpiType.READ, now.year, now.monthValue).get()
        assertEquals(1, kpi100.value)

        val story200 = storyDao.findById(200).get()
        assertEquals(31, story200.readCount)

        val kpi200 =
            kpiDao.findByStoryIdAndTypeAndYearAndMonth(story200.id!!, KpiType.READ, now.year, now.monthValue).get()
        assertEquals(20, kpi200.value)
    }
}
