package com.wutsi.blog.kpi.service.importer

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.service.StoryService
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
class ClickCountKpiImporterTest {
    @Autowired
    private lateinit var storage: TrackingStorageService

    @MockBean
    private lateinit var storyService: StoryService

    @Autowired
    private lateinit var importer: ClickCountKpiImporter

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
            "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/clicks.csv",
            ByteArrayInputStream(
                """
                    account_id,device_id,product_id, total_clicks
                    1,device-x,-,11
                    1,device-1,100,1
                    ,device-2,100,20
                    3,device-3,100,11
                    1,device-1,200,11
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )
        storage.store(
            "kpi/yearly/2023/clicks.csv",
            ByteArrayInputStream(
                """
                    account_id,device_id,product_id, total_clicks
                    1,device-x,-,11
                    1,device-1,100,1
                    ,device-2,100,20
                    3,device-3,100,11
                    5,device-5,100,11
                    ,device-2,200,11
                    ,device-2,300,11
                    ,device-2,400,11
                    ,device-2,500,11
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        val result = importer.import(date)

        assertEquals(5, result)
        verify(storyService).updateClickCount(100, 4L)
        verify(storyService).updateClickCount(200, 1L)
    }
}
