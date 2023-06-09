package com.wutsi.blog.kpi.it

import com.wutsi.blog.kpi.dao.KpiMonthlyRepository
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.dao.StoryRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.jdbc.Sql
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/kpi/ReplayKpiCommand.sql"])
internal class ReplayKpiCommandExecutor {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var storage: TrackingStorageService

    @Autowired
    private lateinit var storyDao: StoryRepository

    @Autowired
    private lateinit var kpiDao: KpiMonthlyRepository

    @Test
    fun run() {
        // GIVEN
        val date1 = LocalDate.now()
        storage.store(
            "kpi/monthly/" + date1.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv",
            ByteArrayInputStream(
                """
                    product_id, total_reads
                    100,1
                """.trimIndent().toByteArray()
            ),
            "application/json"
        )

        val date2 = if (date1.monthValue == 12) {
            date1.minusMonths(1)
        } else {
            date1.plusMonths(1)
        }
        storage.store(
            "kpi/monthly/" + date2.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv",
            ByteArrayInputStream(
                """
                    product_id, total_reads
                    200,20
                """.trimIndent().toByteArray()
            ),
            "application/json"
        )

        // WHEN
        rest.getForEntity(
            "/v1/kpis/commands/replay?year=${date1.year}",
            Any::class.java,
        )

        // THEN
        val story100 = storyDao.findById(100).get()
        assertEquals(1, story100.readCount)

        val kpi100 =
            kpiDao.findByStoryIdAndTypeAndYearAndMonth(story100.id!!, KpiType.READ, date1.year, date1.monthValue).get()
        assertEquals(1, kpi100.value)

        val story200 = storyDao.findById(200).get()
        assertEquals(31, story200.readCount)

        val kpi200 =
            kpiDao.findByStoryIdAndTypeAndYearAndMonth(story200.id!!, KpiType.READ, date2.year, date2.monthValue).get()
        assertEquals(20, kpi200.value)
    }
}
