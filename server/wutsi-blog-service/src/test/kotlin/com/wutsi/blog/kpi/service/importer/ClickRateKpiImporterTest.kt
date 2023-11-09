package com.wutsi.blog.kpi.service.importer

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.kpi.dao.StoryKpiRepository
import com.wutsi.blog.kpi.domain.StoryKpiEntity
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
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
import java.util.Optional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClickRateKpiImporterTest {
    @Autowired
    private lateinit var storage: TrackingStorageService

    @MockBean
    private lateinit var dao: StoryKpiRepository

    @Autowired
    private lateinit var importer: ClickRateKpiImporter

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    private val date = LocalDate.now()

    @BeforeEach
    fun setUp() {
        File(storageDir).deleteRecursively()

        storage.store(
            "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/clicks.csv",
            ByteArrayInputStream(
                """
                    account_id,device_id,product_id, total_clicks
                    1,device-x,-,11
                    1,device-1,100,1
                    ,device-2,100,20
                    3,device-3,100,11
                """.trimIndent().toByteArray(),
            ),
            "application/json",
        )
    }

    @Test
    fun add() {
        // GIVEN
        doReturn(Optional.of(StoryKpiEntity(value = 10)))
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.CLICK), any(), any(), any())

        doReturn(Optional.of(StoryKpiEntity(value = 100)))
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.READER), any(), any(), any())

        doReturn(Optional.empty<StoryKpiEntity>())
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.CLICK_RATE), any(), any(), any())

        val result = importer.import(date)

        assertEquals(1, result)
        verify(dao).save(
            StoryKpiEntity(
                type = KpiType.CLICK_RATE,
                year = date.year,
                month = date.monthValue,
                value = 1000, // 10%
                storyId = 100,
                source = TrafficSource.ALL,
            )
        )
    }

    @Test
    fun update() {
        // GIVEN
        doReturn(Optional.of(createKPI(value = 10)))
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.CLICK), any(), any(), any())

        doReturn(Optional.of(createKPI(value = 100)))
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.READER), any(), any(), any())

        doReturn(Optional.of(createKPI(id = 5, value = 555)))
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.CLICK_RATE), any(), any(), any())

        val result = importer.import(date)

        assertEquals(1, result)
        verify(dao).save(
            StoryKpiEntity(
                type = KpiType.CLICK_RATE,
                year = date.year,
                month = date.monthValue,
                value = 1000, // 10%
                storyId = 100,
                source = TrafficSource.ALL,
                id = 5
            )
        )
    }

    @Test
    fun `no click`() {
        // GIVEN
        doReturn(Optional.empty<StoryKpiEntity>())
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.CLICK), any(), any(), any())

        doReturn(Optional.of(createKPI(value = 100)))
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.READER), any(), any(), any())

        val kpi = createKPI(id = 5, value = 555)
        doReturn(Optional.of(kpi))
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.CLICK_RATE), any(), any(), any())

        val result = importer.import(date)

        assertEquals(1, result)
        verify(dao).delete(kpi)
    }

    @Test
    fun `no reader`() {
        // GIVEN
        doReturn(Optional.of(createKPI(value = 100)))
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.CLICK), any(), any(), any())

        doReturn(Optional.empty<StoryKpiEntity>())
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.READER), any(), any(), any())

        val kpi = createKPI(id = 5, value = 555)
        doReturn(Optional.of(kpi))
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.CLICK_RATE), any(), any(), any())

        val result = importer.import(date)

        assertEquals(1, result)
        verify(dao).delete(kpi)
    }


    @Test
    fun `no op`() {
        // GIVEN
        doReturn(Optional.of(createKPI(value = 0)))
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.CLICK), any(), any(), any())

        doReturn(Optional.of(createKPI(value = 100)))
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.READER), any(), any(), any())

        doReturn(Optional.empty<StoryKpiEntity>())
            .whenever(dao)
            .findByStoryIdAndTypeAndYearAndMonthAndSource(any(), eq(KpiType.CLICK_RATE), any(), any(), any())

        val result = importer.import(date)

        assertEquals(1, result)
        verify(dao, never()).delete(any())
        verify(dao, never()).save(any())
    }

    private fun createKPI(value: Long, id: Long? = null) = StoryKpiEntity(
        type = KpiType.CLICK_RATE,
        year = date.year,
        month = date.monthValue,
        value = value,
        storyId = 100,
        source = TrafficSource.ALL,
        id = id
    )
}
