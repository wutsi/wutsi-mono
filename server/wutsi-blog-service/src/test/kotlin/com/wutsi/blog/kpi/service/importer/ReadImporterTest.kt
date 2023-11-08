package com.wutsi.blog.kpi.service.importer

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.service.StoryService
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
class ReadImporterTest {
    @Autowired
    private lateinit var storage: TrackingStorageService

    @MockBean
    private lateinit var persister: KpiPersister

    @Autowired
    private lateinit var importer: ReadImporter

    @MockBean
    protected lateinit var storyService: StoryService

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
            "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv",
            ByteArrayInputStream(
                """
                    product_id,total_clicks
                    100,10,
                    200,100
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        doReturn(
            listOf(
                StoryEntity(userId = 1L),
                StoryEntity(userId = 2L),
                StoryEntity(userId = 3L),
            )
        ).whenever(storyService).searchStories(any())

        val result = importer.import(date)

        assertEquals(2, result)
        verify(persister).persistStory(date, KpiType.READ, 100, 10)
        verify(persister).persistStory(date, KpiType.READ, 200, 100)

        verify(persister).persistUser(date, KpiType.READ, 1L, TrafficSource.ALL)
        verify(persister).persistUser(date, KpiType.READ, 2L, TrafficSource.ALL)
        verify(persister).persistUser(date, KpiType.READ, 3L, TrafficSource.ALL)
    }
}
