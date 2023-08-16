package com.wutsi.tracking.manager.service.aggregator.readers

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.service.aggregator.reader.ReaderKey
import com.wutsi.tracking.manager.service.aggregator.reader.ReaderOutputWriter
import com.wutsi.tracking.manager.service.aggregator.reader.ReaderValue
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
internal class ReaderOutputWriterTest {
    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @Autowired
    private lateinit var storage: StorageService

    private val path = "kpi/2020/01/01/readers.csv"

    @BeforeEach
    fun setUp() {
        File("$storageDir/$path").delete()
    }

    @Test
    fun write() {
        val writer = ReaderOutputWriter(path, storage)
        writer.write(
            listOf(
                ReaderValue(ReaderKey("1", "device-1", "1"), 11),
                ReaderValue(ReaderKey("2", "device-2", "2"), 12),
                ReaderValue(ReaderKey(null, "device-n", "9"), 99),
            ),
        )

        val file = File("$storageDir/$path")
        assertTrue(file.exists())
        assertEquals(
            """
                account_id,device_id,product_id,total_reads
                1,device-1,1,11
                2,device-2,2,12
                ,device-n,9,99
            """.trimIndent(),
            IOUtils.toString(FileInputStream(file), Charsets.UTF_8).trimIndent(),
        )
    }
}
