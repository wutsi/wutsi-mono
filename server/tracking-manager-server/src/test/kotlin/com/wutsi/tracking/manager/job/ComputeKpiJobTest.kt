package com.wutsi.tracking.manager.job

import com.amazonaws.util.IOUtils
import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.dao.DailyReadRepository
import com.wutsi.tracking.manager.dao.DailyScrollRepository
import com.wutsi.tracking.manager.dao.MonthlyReadRepository
import com.wutsi.tracking.manager.dao.MonthlyScrollRepository
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.entity.ReadEntity
import com.wutsi.tracking.manager.entity.ScrollEntity
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import com.wutsi.tracking.manager.service.aggregator.scrolls.DailyScrollFilter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.io.FileInputStream
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ComputeKpiJobTest {
    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @Autowired
    private lateinit var dao: TrackRepository

    @Autowired
    private lateinit var dailyReadRepository: DailyReadRepository

    @Autowired
    private lateinit var monthlyReadRepository: MonthlyReadRepository

    @Autowired
    protected lateinit var dailyScrollRepository: DailyScrollRepository

    @Autowired
    private lateinit var monthlyScrollRepository: MonthlyScrollRepository

    @Autowired
    private lateinit var job: ComputeKpiJob

    @BeforeEach
    fun setUp() {
        File("$storageDir/track").deleteRecursively()
        File("$storageDir/kpi").deleteRecursively()
    }

    @Test
    fun run() {
        // GIVEN
        val today = LocalDate.now()
        dao.save(
            listOf(
                /* Read */
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "111",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "222",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
                Fixtures.createTrackEntity(
                    page = "error",
                    time = OffsetDateTime.now().toEpochSecond() * 1000,
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "333",
                    time = today.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),

                /* Scroll */
                Fixtures.createTrackEntity(
                    correlationId = "111.00",
                    page = DailyScrollFilter.PAGE,
                    event = DailyScrollFilter.EVENT,
                    productId = "111",
                    value = "10",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
                Fixtures.createTrackEntity(
                    correlationId = "111.00",
                    page = DailyScrollFilter.PAGE,
                    event = DailyScrollFilter.EVENT,
                    productId = "111",
                    value = "40",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
                Fixtures.createTrackEntity(
                    correlationId = "111.00",
                    page = DailyScrollFilter.PAGE,
                    event = DailyScrollFilter.EVENT,
                    productId = "111",
                    value = "80",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
                Fixtures.createTrackEntity(
                    correlationId = "222.00",
                    page = DailyScrollFilter.PAGE,
                    event = DailyScrollFilter.EVENT,
                    productId = "222",
                    value = "10",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
                Fixtures.createTrackEntity(
                    correlationId = "222.00",
                    page = DailyScrollFilter.PAGE,
                    event = DailyScrollFilter.EVENT,
                    productId = "222",
                    value = "20",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
            ),
        )
        dao.save(
            listOf(
                /* Read */
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "111",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "111",
                    time = today.minusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),

                /* Scroll */
                Fixtures.createTrackEntity(
                    correlationId = "111.01",
                    page = DailyScrollFilter.PAGE,
                    event = DailyScrollFilter.EVENT,
                    productId = "111",
                    value = "10",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
                Fixtures.createTrackEntity(
                    correlationId = "111.02",
                    page = DailyScrollFilter.PAGE,
                    event = DailyScrollFilter.EVENT,
                    productId = "111",
                    value = "30",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
            ),
            today.minusDays(1),
        )

        dailyReadRepository.save(
            listOf(
                ReadEntity(
                    productId = "111",
                    totalReads = 10,
                ),
            ),
            today.minusDays(1),
            "reads.csv",
        )
        monthlyReadRepository.save(
            listOf(
                ReadEntity(
                    productId = "111",
                    totalReads = 20,
                ),
            ),
            today.minusMonths(1),
            "reads.csv",
        )

        dailyScrollRepository.save(
            listOf(
                ScrollEntity(
                    productId = "111",
                    averageScroll = 10,
                ),
            ),
            today.minusDays(1),
            "scrolls.csv",
        )
        monthlyScrollRepository.save(
            listOf(
                ScrollEntity(
                    productId = "111",
                    averageScroll = 20,
                ),
            ),
            today.minusMonths(1),
            "scrolls.csv",
        )

        // WHEN
        job.run()

        // THEN
        assertFile(
            File("$storageDir/kpi/daily/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/reads.csv"),
            """
                product_id,total_reads
                111,2
                222,1
            """.trimIndent(),
        )
        assertFile(
            File("$storageDir/kpi/monthly/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv"),
            """
                product_id,total_reads
                111,12
                222,1
            """.trimIndent(),
        )
        assertFile(
            File("$storageDir/kpi/yearly/" + today.format(DateTimeFormatter.ofPattern("yyyy")) + "/reads.csv"),
            """
                product_id,total_reads
                111,32
                222,1
            """.trimIndent(),
        )

        assertFile(
            File("$storageDir/kpi/daily/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/scrolls.csv"),
            """
                product_id,average_scrolls
                111,10
                111,30
                111,80
                222,20
            """.trimIndent(),
        )
        assertFile(
            File("$storageDir/kpi/monthly/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/scrolls.csv"),
            """
                product_id,average_scrolls
                111,32
                222,20
            """.trimIndent(),
        )
        assertFile(
            File("$storageDir/kpi/yearly/" + today.format(DateTimeFormatter.ofPattern("yyyy")) + "/scrolls.csv"),
            """
                product_id,average_scrolls
                111,26
                222,20
            """.trimIndent(),
        )
    }

    private fun assertFile(file: File, content: String) {
        assertTrue(file.exists())
        assertEquals(content, IOUtils.toString(FileInputStream(file)).trimIndent())
    }
}
