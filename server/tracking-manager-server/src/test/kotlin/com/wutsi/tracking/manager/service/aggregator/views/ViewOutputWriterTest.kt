package com.wutsi.tracking.manager.service.aggregator.views

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
class ViewOutputWriterTest {
    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @Autowired
    private lateinit var storage: StorageService

    private val path = "kpi/2020/01/01/views.csv"

    @BeforeEach
    fun setUp() {
        File("$storageDir/$path").delete()
    }

    @Test
    fun write() {
        val writer = ViewOutputWriter(path, storage)
        writer.write(
            listOf(
                ViewValue(ViewKey("1"), 11),
                ViewValue(ViewKey("2"), 12),
                ViewValue(ViewKey("9"), 99),
            ),
        )

        val file = File("$storageDir/$path")
        assertTrue(file.exists())
        assertEquals(
            """
                product_id,total_views
                1,11
                2,12
                9,99
            """.trimIndent(),
            IOUtils.toString(FileInputStream(file), Charsets.UTF_8).trimIndent(),
        )
    }
}
