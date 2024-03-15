package com.wutsi.blog.kpi.service.importer

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.service.AdsService
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
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
class AdsImpressionKpiImporterTest {
    @Autowired
    private lateinit var storage: TrackingStorageService

    @MockBean
    private lateinit var persister: KpiPersister

    @Autowired
    private lateinit var importer: AdsImpressionKpiImporter

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
            "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/ads_impressions.csv",
            ByteArrayInputStream(
                """
                    campaing,total_impressions
                    100,10,
                    200,100
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        doReturn(
            listOf(
                AdsEntity(id = "100"),
                AdsEntity(id = "200"),
            )
        ).whenever(adService).findByIds(any())

        val result = importer.import(date)

        assertEquals(2, result)
        verify(persister).persistAds(date, KpiType.IMPRESSION, "100", 10)
        verify(persister).persistAds(date, KpiType.IMPRESSION, "200", 100)

        verify(adService, times(2)).onKpiImported(any())
    }
}