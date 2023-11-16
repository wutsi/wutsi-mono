package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.platform.core.storage.StorageService
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class DurationOutputWriterTest {
    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @Autowired
    private lateinit var storage: StorageService

    private val path = "kpi/2020/01/01/durations.csv"

    @BeforeEach
    fun setUp() {
        File("$storageDir/$path").delete()
    }

    @Test
    fun write() {
        val writer = DurationOutputWriter(path, storage)
        writer.write(
            listOf(
                DurationValue(DurationKey("111", "1"), DurationData("scroll", 11)),
                DurationValue(DurationKey("222", "2"), DurationData("scroll", 12)),
                DurationValue(DurationKey("333", "3"), DurationData("scroll", 99)),
            ),
        )

        val file = File("$storageDir/$path")
        assertTrue(file.exists())
        assertEquals(
            """
                correlation_id,product_id,total_seconds
                111,1,11
                222,2,12
                333,3,99
            """.trimIndent(),
            IOUtils.toString(FileInputStream(file), Charsets.UTF_8).trimIndent(),
        )
    }
}
