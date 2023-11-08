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
class SourceImporterTest {
    @Autowired
    private lateinit var storage: TrackingStorageService

    @MockBean
    private lateinit var persister: KpiPersister

    @Autowired
    private lateinit var importer: SourceImporter

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @MockBean
    protected lateinit var storyService: StoryService

    @BeforeEach
    fun setUp() {
        File(storageDir).deleteRecursively()
    }

    @Test
    fun import() {
        val date = LocalDate.now()
        storage.store(
            "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/source.csv",
            ByteArrayInputStream(
                """
                    product_id,source,total_clicks
                    100,DIRECT,10,
                    100,FACEBOOK,1
                    200,EMAIL,100
                    200,-,3
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )

        doReturn(
            listOf(
                StoryEntity(userId = 1L),
                StoryEntity(userId = 2L),
            )
        ).whenever(storyService).searchStories(any())

        val result = importer.import(date)

        assertEquals(4, result)
        verify(persister).persistStory(date, KpiType.READ, 100, 10, TrafficSource.DIRECT)
        verify(persister).persistStory(date, KpiType.READ, 100, 1, TrafficSource.FACEBOOK)
        verify(persister).persistStory(date, KpiType.READ, 200, 100, TrafficSource.EMAIL)
        verify(persister).persistStory(date, KpiType.READ, 200, 3, TrafficSource.UNKNOWN)

        TrafficSource.values()
            .filter { it != TrafficSource.ALL }
            .forEach {
                verify(persister).persistUser(date, KpiType.READ, 1L, it)
                verify(persister).persistUser(date, KpiType.READ, 2L, it)
            }
    }
}
