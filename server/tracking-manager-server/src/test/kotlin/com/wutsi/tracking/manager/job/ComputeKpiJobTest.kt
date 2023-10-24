package com.wutsi.tracking.manager.job

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.dao.DailyReadRepository
import com.wutsi.tracking.manager.dao.DailyReaderRepository
import com.wutsi.tracking.manager.dao.MonthlyEmailRepository
import com.wutsi.tracking.manager.dao.MonthlyReadRepository
import com.wutsi.tracking.manager.dao.MonthlyReaderRepository
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.entity.EmailEntity
import com.wutsi.tracking.manager.entity.ReadEntity
import com.wutsi.tracking.manager.entity.ReaderEntity
import com.wutsi.tracking.manager.service.aggregator.TrafficSourceDetector
import com.wutsi.tracking.manager.service.aggregator.duration.DailyDurationFilter
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.io.File
import java.io.FileInputStream
import java.time.Clock
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
    private lateinit var dailyReaderRepository: DailyReaderRepository

    @Autowired
    private lateinit var monthlyReaderRepository: MonthlyReaderRepository

    @Autowired
    private lateinit var monthlyEmailRepository: MonthlyEmailRepository

    @Autowired
    private lateinit var dailyDurationRepository: MonthlyEmailRepository

    @Autowired
    private lateinit var job: ComputeKpiJob

    @MockBean
    private lateinit var clock: Clock

    @BeforeEach
    fun setUp() {
        File("$storageDir/track").deleteRecursively()
        File("$storageDir/kpi").deleteRecursively()
    }

    @Test
    fun run() {
        // GIVEN
        val today = LocalDate.of(2023, 8, 14)
        doReturn(today.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()).whenever(clock).millis()

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
                    correlationId = "11111"
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyDurationFilter.EVENT_END,
                    productId = "111",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000 + 60 * 1000,
                    accountId = null,
                    deviceId = "device-n",
                    url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_from=read-also",
                    correlationId = "11111"
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "222",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                    accountId = "2",
                    deviceId = "device-2",
                    url = "https://www.wutsi.com/read/123/this-is-nice",
                    referer = TrafficSourceDetector.EMAIL_REFERER,
                    correlationId = "11112"
                ),
                Fixtures.createTrackEntity(
                    page = "error",
                    time = OffsetDateTime.now().toEpochSecond() * 1000,
                    accountId = "2",
                    deviceId = "device-2",
                    url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test",
                    correlationId = "11113"
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "333",
                    time = today.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                    accountId = "2",
                    deviceId = "device-2",
                    url = "https://www.wutsi.com/read/123/this-is-nice?utm_from=blog",
                    correlationId = "11114"
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
                    url = "https://www.wutsi.com/read/123/this-is-nice?utm_campaign=test&utm_from=read-also",
                    correlationId = "22222"
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyDurationFilter.EVENT_END,
                    productId = "111",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000 + 15 * 1000,
                    accountId = "2",
                    deviceId = "device-2",
                    url = "https://www.wutsi.com/read/123/this-is-nice?utm_campaign=test&utm_from=read-also",
                    correlationId = "22222"
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "111",
                    time = yesterday.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                    accountId = "1",
                    deviceId = "device-1",
                    correlationId = "22223"
                ),
                Fixtures.createTrackEntity(
                    page = DailyReadFilter.PAGE,
                    event = DailyReadFilter.EVENT,
                    productId = "111",
                    deviceId = "device-1",
                    time = yesterday.minusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                    correlationId = "22224"
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
        monthlyEmailRepository.save(
            listOf(
                EmailEntity(
                    accountId = "1",
                    productId = "111",
                    totalReads = 20,
                ),
                EmailEntity(
                    accountId = "2",
                    productId = "222",
                    totalReads = 5,
                ),
            ),
            today.minusMonths(1),
            "emails.csv",
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

        assertFile(
            File("$storageDir/kpi/daily/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/emails.csv"),
            """
                account_id,product_id,total_reads
                2,222,1
            """.trimIndent(),
        )
        assertFile(
            File("$storageDir/kpi/monthly/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/emails.csv"),
            """
                account_id,product_id,total_reads
                2,222,1
            """.trimIndent(),
        )
        assertFile(
            File("$storageDir/kpi/yearly/" + today.format(DateTimeFormatter.ofPattern("yyyy")) + "/emails.csv"),
            """
                account_id,product_id,total_reads
                1,111,20
                2,222,6
            """.trimIndent(),
        )

        assertFile(
            File("$storageDir/kpi/daily/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/durations.csv"),
            """
                correlation_id,product_id,total_seconds
                22222,111,15
                11111,111,60
                11112,222,0
            """.trimIndent(),
        )
        assertFile(
            File("$storageDir/kpi/monthly/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/durations.csv"),
            """
                correlation_id,product_id,total_seconds
                -,111,75
                -,222,0
            """.trimIndent(),
        )
        assertFile(
            File("$storageDir/kpi/yearly/" + today.format(DateTimeFormatter.ofPattern("yyyy")) + "/durations.csv"),
            """
                correlation_id,product_id,total_seconds
                -,111,75
                -,222,0
            """.trimIndent(),
        )
    }

    private fun assertFile(file: File, content: String) {
        assertTrue(file.exists())
        assertEquals(content, IOUtils.toString(FileInputStream(file), Charsets.UTF_8).trimIndent())
    }
}
