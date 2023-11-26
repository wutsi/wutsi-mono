package com.wutsi.blog.kpi.it

import com.wutsi.blog.kpi.dao.StoryKpiRepository
import com.wutsi.blog.kpi.dao.UserKpiRepository
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.job.KpiMonthlyImporterJob
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.dao.ReaderRepository
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

    @Autowired
    protected lateinit var readerDao: ReaderRepository

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
                    100,11
                    200,20
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        storage.store(
            "kpi/monthly/" + now.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/durations.csv",
            ByteArrayInputStream(
                """
                    correlation_id,product_id,total_seconds
                    -,100,1000
                    -,200,1500
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        storage.store(
            "kpi/monthly/" + now.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/readers.csv",
            ByteArrayInputStream(
                """
                    account_id,device_id,product_id, total_reads
                    1,device-1,100,2
                    ,device-2,100,1
                    3,device-3,100,2
                    1,device-1,200,11
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )
        storage.store(
            "kpi/yearly/2020/readers.csv",
            ByteArrayInputStream(
                """
                    account_id,device_id,product_id, total_reads
                    1,device-x,-,11
                    1,device-1,100,1
                    ,device-2,100,20
                    3,device-3,100,11
                    ,device-2,200,11
                    ,device-2,300,11
                    ,device-2,400,11
                    ,device-2,500,11
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )
        storage.store(
            "kpi/yearly/2021/readers.csv",
            ByteArrayInputStream(
                """
                    account_id,device_id,product_id, total_reads
                    5,device-5,100,10
                    ,device-6,100,20
                    1,device-1,100,11
                    3,device-1,100,43
                    5,device-1,200,555
                    1,device-1,300,11
                    1,device-1,400,11
                    1,device-1,500,11
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        storage.store(
            "kpi/monthly/" + now.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/emails.csv",
            ByteArrayInputStream(
                """
                    account_id,product_id,total_reads
                    1,100,1
                    1,200,11
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        storage.store(
            "kpi/monthly/" + now.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/source.csv",
            ByteArrayInputStream(
                """
                    product_id,source,total_clicks
                    100,DIRECT,10,
                    100,FACEBOOK,1
                    200,EMAIL,100
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        storage.store(
            "kpi/monthly/" + now.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/clicks.csv",
            ByteArrayInputStream(
                """
                    account_id,device_id,product_id, total_clicks
                    1,device-1,100,1
                    ,device-2,100,20
                    3,device-3,100,11
                    1,device-1,200,11
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )
        storage.store(
            "kpi/yearly/2023/clicks.csv",
            ByteArrayInputStream(
                """
                    account_id,device_id,product_id, total_clicks
                    1,device-x,-,11
                    1,device-1,100,1
                    ,device-2,100,20
                    3,device-3,100,11
                    5,device-5,100,11
                    ,device-2,200,11
                    ,device-2,300,11
                    ,device-2,400,11
                    ,device-2,500,11
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        // WHEN
        job.run()

        // THEN
        validateStory(now)
        validateUser(now)
        validateReader()
    }

    private fun validateUser(now: LocalDate) {
        val user = userDao.findById(111).get()
        assertEquals(22, user.readCount)
        assertEquals(2, user.clickCount)
        assertEquals(2011, user.totalDurationSeconds)

        assertEquals(
            2,
            userKpiDao.findByUserIdAndTypeAndYearAndMonthAndSource(
                111,
                KpiType.SUBSCRIPTION,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )

        assertEquals(
            11,
            userKpiDao.findByUserIdAndTypeAndYearAndMonthAndSource(
                111,
                KpiType.READ,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )
        assertEquals(
            10,
            userKpiDao.findByUserIdAndTypeAndYearAndMonthAndSource(
                111,
                KpiType.READ,
                now.year,
                now.monthValue,
                TrafficSource.DIRECT
            ).get().value
        )
        assertEquals(
            1,
            userKpiDao.findByUserIdAndTypeAndYearAndMonthAndSource(
                111,
                KpiType.READ,
                now.year,
                now.monthValue,
                TrafficSource.FACEBOOK
            ).get().value
        )

        assertEquals(
            3,
            userKpiDao.findByUserIdAndTypeAndYearAndMonthAndSource(
                111,
                KpiType.LIKE,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )

        assertEquals(
            5,
            userKpiDao.findByUserIdAndTypeAndYearAndMonthAndSource(
                111,
                KpiType.COMMENT,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )
    }

    private fun validateStory(now: LocalDate) {
        val story = storyDao.findById(100).get()
        assertEquals(11, story.readCount)
        assertEquals(4, story.clickCount)
        assertEquals(1000, story.totalDurationSeconds)
        assertEquals(1, story.emailReaderCount)
        assertEquals(5, story.readerCount)
        assertEquals(2, story.subscriberCount)

        assertEquals(
            11,
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                100,
                KpiType.READ,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )
        assertEquals(
            10,
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                100,
                KpiType.READ,
                now.year,
                now.monthValue,
                TrafficSource.DIRECT
            ).get().value
        )
        assertEquals(
            1,
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                100,
                KpiType.READ,
                now.year,
                now.monthValue,
                TrafficSource.FACEBOOK
            ).get().value
        )

        assertEquals(
            3,
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                100,
                KpiType.READER,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )

        assertEquals(
            1,
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                100,
                KpiType.READER_EMAIL,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )

        assertEquals(
            1000,
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                100,
                KpiType.DURATION,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )

        assertEquals(
            3,
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                100,
                KpiType.CLICK,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )

        assertEquals(
            10000,
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                100,
                KpiType.CLICK_RATE,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )

        assertEquals(
            2,
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                100,
                KpiType.LIKE,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )
        assertEquals(
            1,
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                101,
                KpiType.LIKE,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )

        assertEquals(
            4,
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                100,
                KpiType.COMMENT,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )
        assertEquals(
            1,
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                101,
                KpiType.COMMENT,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )

        assertEquals(
            2,
            storyKpiDao.findByStoryIdAndTypeAndYearAndMonthAndSource(
                100,
                KpiType.SUBSCRIPTION,
                now.year,
                now.monthValue,
                TrafficSource.ALL
            ).get().value
        )
    }

    private fun validateReader() {
        assertEquals(
            true,
            readerDao.findByUserIdAndStoryId(1, 100).get().email
        )
        assertEquals(
            false,
            readerDao.findByUserIdAndStoryId(3, 100).get().email
        )
    }
}
