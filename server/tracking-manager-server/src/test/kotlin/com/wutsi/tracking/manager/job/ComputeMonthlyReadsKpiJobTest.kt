package com.wutsi.tracking.manager.job

import com.amazonaws.util.IOUtils
import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.dao.DailyReadRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.io.FileInputStream
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ComputeMonthlyReadsKpiJobTest {
    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @Autowired
    private lateinit var dao: DailyReadRepository

    @Autowired
    private lateinit var job: ComputeMonthlyReadsKpiJob

    @BeforeEach
    fun setUp() {
        File("$storageDir/kpi").deleteRecursively()
    }

    @Test
    fun run() {
        // GIVEN
        val today = LocalDate.now(ZoneId.of("UTC"))
        dao.save(
            listOf(
                Fixtures.createReadEntity(
                    productId = "111",
                    totalReads = 100,
                ),
                Fixtures.createReadEntity(
                    productId = "222",
                    totalReads = 200,
                ),
            ),
            LocalDate.of(today.year, today.month, 1),
        )
        dao.save(
            listOf(
                Fixtures.createReadEntity(
                    productId = "111",
                    totalReads = 1,
                ),
                Fixtures.createReadEntity(
                    productId = "222",
                    totalReads = 2,
                ),
                Fixtures.createReadEntity(
                    productId = "333",
                    totalReads = 3,
                ),
            ),
            LocalDate.of(today.year, today.month, 2),
        )

        // WHEN
        job.run()

        // THEN
        val file =
            File("$storageDir/kpi/monthly/" + today.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv")
        assertTrue(file.exists())
        assertEquals(
            """
                product_id,total_reads
                111,101
                222,202
                333,3
            """.trimIndent(),
            IOUtils.toString(FileInputStream(file)).trimIndent(),
        )
    }
}
