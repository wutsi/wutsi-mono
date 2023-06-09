package com.wutsi.tracking.manager.service.aggregator.reads

import com.amazonaws.util.IOUtils
import com.wutsi.platform.core.storage.StorageService
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
internal class ReadOutputWriterTest {
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
        val writer = ReadOutputWriter(path, storage)
        writer.write(
            listOf(
                Read(ReadKey("1"), 11),
                Read(ReadKey("2"), 12),
                Read(ReadKey("9"), 99),
            ),
        )

        val file = File("$storageDir/$path")
        assertTrue(file.exists())
        assertEquals(
            """
                product_id,total_reads
                1,11
                2,12
                9,99
            """.trimIndent(),
            IOUtils.toString(FileInputStream(file)).trimIndent(),
        )
    }
}
