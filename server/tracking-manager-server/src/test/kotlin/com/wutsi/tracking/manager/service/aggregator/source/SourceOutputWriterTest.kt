package com.wutsi.tracking.manager.service.aggregator.source

import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.io.FileInputStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class SourceOutputWriterTest {
    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @Autowired
    private lateinit var storage: StorageService

    private val path = "kpi/2020/01/01/reads.csv"

    @BeforeEach
    fun setUp() {
        File("$storageDir/$path").delete()
    }

    @Test
    fun write() {
        val writer = SourceOutputWriter(path, storage)
        writer.write(
            listOf(
                SourceValue(SourceKey("1", TrafficSource.EMAIL), 11),
                SourceValue(SourceKey("2", TrafficSource.DIRECT), 12),
                SourceValue(SourceKey("9", TrafficSource.EMAIL), 99),
            ),
        )

        val file = File("$storageDir/$path")
        assertTrue(file.exists())
        assertEquals(
            """
                product_id,source,total_reads
                1,EMAIL,11
                2,DIRECT,12
                9,EMAIL,99
            """.trimIndent(),
            IOUtils.toString(FileInputStream(file), Charsets.UTF_8).trimIndent(),
        )
    }
}
