package com.wutsi.blog.kpi.service.importer

import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.ads.service.AdsService
import com.wutsi.blog.kpi.service.TrackingStorageService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdsImpressionTodayKpiImporterTest {
    @Autowired
    private lateinit var storage: TrackingStorageService

    @Autowired
    private lateinit var importer: AdsImpressionTodayKpiImporter

    @MockBean
    protected lateinit var adService: AdsService

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @BeforeEach
    fun setUp() {
        File(storageDir).deleteRecursively()
    }

    @Test
    fun import() {
        val date = LocalDate.now()
        storage.store(
            "kpi/daily/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/ads_impressions.csv",
            ByteArrayInputStream(
                """
                    campaing,total_impressions
                    100,10,
                    200,100
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        val result = importer.import(date)

        Assertions.assertEquals(2, result)
        verify(adService).onTodayImpressionImported("100", 10L)
        verify(adService).onTodayImpressionImported("200", 100L)
    }

    @Test
    fun badDate() {
        val date = LocalDate.now().plusDays(-1)
        storage.store(
            "kpi/daily/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/ads_impressions.csv",
            ByteArrayInputStream(
                """
                    campaing,total_impressions
                    100,10,
                    200,100
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        val result = importer.import(date)

        assertEquals(0L, result)
        verify(adService, never()).onTodayImpressionImported("100", 10L)
    }
}