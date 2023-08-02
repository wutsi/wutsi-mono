package com.wutsi.tracking.manager.job

import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.dao.DailyReadRepository
import com.wutsi.tracking.manager.dao.DailyReaderRepository
import com.wutsi.tracking.manager.dao.MonthlyReadRepository
import com.wutsi.tracking.manager.dao.MonthlyReaderRepository
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.entity.ReadEntity
import com.wutsi.tracking.manager.entity.ReaderEntity
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import org.apache.commons.io.IOUtils
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
import java.time.ZoneId
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
    private lateinit var dailyReaderRepository: DailyReaderRepository

    @Autowired
    private lateinit var monthlyReaderRepository: MonthlyReaderRepository

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
        val today = LocalDate.now(ZoneId.of("UTC"))
        val yesterday = today.minusDays(1)
        dao.save(
            listOf(
                /* Read */
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "111",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                    accountId = null,
                    deviceId = "device-n",
                    url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_from=read-also",
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "222",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                    accountId = "2",
                    deviceId = "device-2",
                    url = "https://www.wutsi.com/read/123/this-is-nice",
                ),
                Fixtures.createTrackEntity(
                    page = "error",
                    time = OffsetDateTime.now().toEpochSecond() * 1000,
                    accountId = "2",
                    deviceId = "device-2",
                    url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test",
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "333",
                    time = today.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                    accountId = "2",
                    deviceId = "device-2",
                    url = "https://www.wutsi.com/read/123/this-is-nice?utm_from=blog",
                ),
            ),
            today,
        )
        dao.save(
            listOf(
                /* Read */
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "111",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                    accountId = "2",
                    deviceId = "device-2",
                    url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_from=read-also",
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "111",
                    time = yesterday.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                    accountId = "1",
                    deviceId = "device-1",
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "111",
                    deviceId = "device-1",
                    time = yesterday.minusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
            ),
            yesterday,
        )

        dailyReadRepository.save(
            listOf(
                ReadEntity(
                    productId = "111",
                    totalReads = 10,
                ),
            ),
            yesterday,
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

        dailyReaderRepository.save(
            listOf(
                ReaderEntity(
                    accountId = "2",
                    deviceId = "device-2",
                    productId = "222",
                    totalReads = 6,
                ),
                ReaderEntity(
                    accountId = null,
                    deviceId = "device-n",
                    productId = "111",
                    totalReads = 4,
                ),
            ),
            yesterday,
            "readers.csv",
        )
        monthlyReaderRepository.save(
            listOf(
                ReaderEntity(
                    accountId = "1",
                    deviceId = "device-1",
                    productId = "111",
                    totalReads = 11,
                ),
            ),
            today.minusMonths(1),
            "readers.csv",
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
            File("$storageDir/kpi/daily/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/readers.csv"),
            """
                account_id,device_id,product_id,total_reads
                2,device-2,111,1
                "",device-n,111,1
                2,device-2,222,1
            """.trimIndent(),
        )
        assertFile(
            File("$storageDir/kpi/monthly/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/readers.csv"),
            """
                account_id,device_id,product_id,total_reads
                2,device-2,222,7
                "",device-n,111,5
                2,device-2,111,1
            """.trimIndent(),
        )
        assertFile(
            File("$storageDir/kpi/yearly/" + today.format(DateTimeFormatter.ofPattern("yyyy")) + "/readers.csv"),
            """
                account_id,device_id,product_id,total_reads
                1,device-1,111,11
                2,device-2,222,7
                "",device-n,111,5
                2,device-2,111,1
            """.trimIndent(),
        )

        assertFile(
            File("$storageDir/kpi/daily/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/from.csv"),
            """
                from,total_reads
                read-also,2
                DIRECT,1
            """.trimIndent(),
        )
        assertFile(
            File("$storageDir/kpi/monthly/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/from.csv"),
            """
                from,total_reads
                read-also,2
                DIRECT,1
            """.trimIndent(),
        )
        assertFile(
            File("$storageDir/kpi/yearly/" + today.format(DateTimeFormatter.ofPattern("yyyy")) + "/from.csv"),
            """
                from,total_reads
                read-also,2
                DIRECT,1
            """.trimIndent(),
        )
    }

    private fun assertFile(file: File, content: String) {
        assertTrue(file.exists())
        assertEquals(content, IOUtils.toString(FileInputStream(file), Charsets.UTF_8).trimIndent())
    }
}
