package com.wutsi.tracking.manager.service.aggregator.from

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
internal class FromOutputWriterTest {
    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @Autowired
    private lateinit var storage: StorageService

    private val path = "kpi/2020/01/01/from.csv"

    @BeforeEach
    fun setUp() {
        File("$storageDir/$path").delete()
    }

    @Test
    fun write() {
        val writer = FromOutputWriter(path, storage)
        writer.write(
            listOf(
                FromValue(FromKey("read-also"), 11L),
                FromValue(FromKey("blog"), 12L),
                FromValue(FromKey("home"), 99L),
            ),
        )

        val file = File("$storageDir/$path")
        assertTrue(file.exists())
        assertEquals(
            """
                from,total_reads
                read-also,11
                blog,12
                home,99
            """.trimIndent(),
            IOUtils.toString(FileInputStream(file), Charsets.UTF_8).trimIndent(),
        )
    }
}
