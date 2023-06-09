package com.wutsi.tracking.manager.endpoint

import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.client.RestTemplate
import java.io.File
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ReplayKpiControllerTest {
    @LocalServerPort
    public val port: Int = 0

    private val rest = RestTemplate()

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @Autowired
    private lateinit var dao: TrackRepository

    private val jan = LocalDate.of(LocalDate.now().year, 1, 1)
    private val feb = jan.plusMonths(1)
    private val lastYear = jan.minusMonths(3)

    @BeforeEach
    fun setUp() {
        File("$storageDir/kpi").deleteRecursively()
        File("$storageDir/track").deleteRecursively()

        // GIVEN
        dao.save(
            listOf(
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "111",
                    time = jan.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "222",
                    time = jan.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
                Fixtures.createTrackEntity(page = "error", time = OffsetDateTime.now().toEpochSecond() * 1000),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "333",
                    time = jan.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
            ),
            jan,
        )
        dao.save(
            listOf(
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "111",
                    time = feb.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "111",
                    time = feb.minusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
            ),
            feb,
        )
        dao.save(
            listOf(
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "111",
                    time = lastYear.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
            ),
            lastYear,
        )
    }

    @Test
    fun year() {
        // WHEN
        val url = "http://localhost:$port/v1/kpis/replay?year=${jan.year}"
        rest.getForEntity(url, Any::class.java)

        // THEN
        assertTrue(File("$storageDir/kpi/daily/" + jan.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/reads.csv").exists())
        assertTrue(File("$storageDir/kpi/daily/" + feb.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/reads.csv").exists())
        assertFalse(File("$storageDir/kpi/daily/" + lastYear.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/reads.csv").exists())

        assertTrue(File("$storageDir/kpi/monthly/" + jan.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv").exists())
        assertTrue(File("$storageDir/kpi/monthly/" + feb.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv").exists())
        assertFalse(File("$storageDir/kpi/monthly/" + lastYear.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv").exists())

        assertTrue(File("$storageDir/kpi/yearly/" + jan.format(DateTimeFormatter.ofPattern("yyyy")) + "/reads.csv").exists())
    }

    @Test
    fun month() {
        // WHEN
        val url = "http://localhost:$port/v1/kpis/replay?year=${jan.year}&month=${jan.month.value}"
        rest.getForEntity(url, Any::class.java)

        // THEN
        assertTrue(File("$storageDir/kpi/daily/" + jan.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/reads.csv").exists())
        assertFalse(File("$storageDir/kpi/daily/" + feb.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/reads.csv").exists())
        assertFalse(File("$storageDir/kpi/daily/" + lastYear.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/reads.csv").exists())

        assertTrue(File("$storageDir/kpi/monthly/" + jan.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv").exists())
        assertFalse(File("$storageDir/kpi/monthly/" + feb.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv").exists())
        assertFalse(File("$storageDir/kpi/monthly/" + lastYear.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv").exists())

        assertTrue(File("$storageDir/kpi/yearly/" + jan.format(DateTimeFormatter.ofPattern("yyyy")) + "/reads.csv").exists())
    }
}
