package com.wutsi.blog.kpi.service.importer

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.service.ReaderService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.io.ByteArrayInputStream
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmailKpiImporterTest {
    @Autowired
    private lateinit var storage: TrackingStorageService

    @MockBean
    private lateinit var persister: KpiPersister

    @Autowired
    private lateinit var importer: EmailKpiImporter

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @MockBean
    private lateinit var readerService: ReaderService

    @BeforeEach
    fun setUp() {
        File(storageDir).deleteRecursively()
    }

    @Test
    fun import() {
        val date = LocalDate.now()
        storage.store(
            "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/emails.csv",
            ByteArrayInputStream(
                """
                    account_id,product_id,total_reads
                    1,-,11
                    1,100,1
                    3,100,11
                    1,200,11
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        val result = importer.import(date)

        assertEquals(4, result)
        verify(persister).persistStory(date, KpiType.READER_EMAIL, 100, 2)
        verify(persister).persistStory(date, KpiType.READER_EMAIL, 200, 1)

        verify(readerService).storeReader(1L, 100, email = true)
        verify(readerService).storeReader(3L, 100, email = true)
        verify(readerService).storeReader(1L, 200, email = true)
    }
}
