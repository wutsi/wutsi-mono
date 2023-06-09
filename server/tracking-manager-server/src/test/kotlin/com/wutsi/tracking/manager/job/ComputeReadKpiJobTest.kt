package com.wutsi.tracking.manager.job

import com.amazonaws.util.IOUtils
import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.dao.DailyReadRepository
import com.wutsi.tracking.manager.dao.MonthlyReadRepository
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.entity.ReadEntity
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
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
internal class ComputeReadKpiJobTest {
    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @Autowired
    private lateinit var dao: TrackRepository

    @Autowired
    private lateinit var dailyReadRepository: DailyReadRepository

    @Autowired
    private lateinit var monthlyReadRepository: MonthlyReadRepository

    @Autowired
    private lateinit var job: ComputeReadsKpiJob

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
                Fixtures.createTrackEntity(page = "error", time = OffsetDateTime.now().toEpochSecond() * 1000),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "333",
                    time = today.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
            ),
        )
        dao.save(
            listOf(
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

        // WHEN
        job.run()

        // THEN
        val dailyFile =
            File("$storageDir/kpi/daily/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/reads.csv")
        assertTrue(dailyFile.exists())
        assertEquals(
            """
                product_id,total_reads
                111,2
                222,1
            """.trimIndent(),
            IOUtils.toString(FileInputStream(dailyFile)).trimIndent(),
        )

        val monthlyFile =
            File("$storageDir/kpi/monthly/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv")
        assertTrue(monthlyFile.exists())
        assertEquals(
            """
                product_id,total_reads
                111,12
                222,1
            """.trimIndent(),
            IOUtils.toString(FileInputStream(monthlyFile)).trimIndent(),
        )

        val yearlyFile =
            File("$storageDir/kpi/yearly/" + today.format(DateTimeFormatter.ofPattern("yyyy")) + "/reads.csv")
        assertTrue(yearlyFile.exists())
        assertEquals(
            """
                product_id,total_reads
                111,32
                222,1
            """.trimIndent(),
            IOUtils.toString(FileInputStream(yearlyFile)).trimIndent(),
        )
    }
}
