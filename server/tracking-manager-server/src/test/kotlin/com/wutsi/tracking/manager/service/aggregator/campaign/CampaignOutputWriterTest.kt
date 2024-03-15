package com.wutsi.tracking.manager.service.aggregator.campaign

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
class CampaignOutputWriterTest {
    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @Autowired
    private lateinit var storage: StorageService

    private val path = "kpi/2020/01/01/ads_impressions.csv"

    @BeforeEach
    fun setUp() {
        File("$storageDir/$path").delete()
    }

    @Test
    fun write() {
        val writer = CampaignOutputWriter(path, storage)
        writer.write(
            listOf(
                CampaignValue(CampaignKey("campaign-1"), 11),
                CampaignValue(CampaignKey("campaign-2"), 12),
                CampaignValue(CampaignKey("campaign-9"), 99),
            ),
        )

        val file = File("$storageDir/$path")
        assertTrue(file.exists())
        assertEquals(
            """
                campaign,total_impressions
                campaign-1,11
                campaign-2,12
                campaign-9,99
            """.trimIndent(),
            IOUtils.toString(FileInputStream(file), Charsets.UTF_8).trimIndent(),
        )
    }
}