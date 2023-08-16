package com.wutsi.blog.kpi.it

import com.wutsi.blog.kpi.dao.StoryKpiRepository
import com.wutsi.blog.kpi.dao.UserKpiRepository
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.dao.StoryRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.jdbc.Sql
import java.io.ByteArrayInputStream
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/kpi/ReplayKpiCommand.sql"])
internal class ReplayKpiCommandTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var storage: TrackingStorageService

    @Autowired
    private lateinit var storyDao: StoryRepository

    @Autowired
    private lateinit var storyKpiDao: StoryKpiRepository

    @Autowired
    private lateinit var userKpiDao: UserKpiRepository

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @BeforeEach
    fun setUp() {
        File(storageDir).deleteRecursively()
    }

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
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        val date2 = date1.minusMonths(1)
        storage.store(
            "kpi/monthly/" + date2.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv",
            ByteArrayInputStream(
                """
                    product_id, total_reads
                    200,20
                """.trimIndent().toByteArray(),
            ),
            "application/json",
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
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                story100.id!!,
                KpiType.READ,
                date1.year,
                date1.monthValue,
                TrafficSource.ALL,
            ).get()
        assertEquals(1, kpi100.value)

        val story200 = storyDao.findById(200).get()
        assertEquals(31, story200.readCount)

        val kpi200 =
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                story200.id!!,
                KpiType.READ,
                date2.year,
                date2.monthValue,
                TrafficSource.ALL,
            )
                .get()
        assertEquals(20, kpi200.value)

        val user111 =
            userKpiDao.findByUserIdAndTypeAndYearAndMonthAndSource(
                story100.userId,
                KpiType.READ,
                date1.year,
                date1.monthValue,
                TrafficSource.ALL,
            )
                .get()
        assertEquals(1, user111.value)

        val user211 =
            userKpiDao.findByUserIdAndTypeAndYearAndMonthAndSource(
                story200.userId,
                KpiType.READ,
                date2.year,
                date2.monthValue,
                TrafficSource.ALL,
            )
                .get()
        assertEquals(20, user211.value)
    }
}
