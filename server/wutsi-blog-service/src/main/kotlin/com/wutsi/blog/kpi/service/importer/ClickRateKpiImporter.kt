package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dao.StoryKpiRepository
import com.wutsi.blog.kpi.domain.StoryKpiEntity
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.jvm.optionals.getOrNull

@Service
class ClickRateKpiImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
    private val dao: StoryKpiRepository,
) : AbstractImporter(storage, persister) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ClickRateKpiImporter::class.java)
    }

    override fun getFilePath(date: LocalDate) =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/clicks.csv"

    @Transactional
    override fun import(date: LocalDate): Long {
        return super.import(date)
    }

    override fun import(date: LocalDate, file: File): Long {
        // Load Story-ids
        val storyIds = loadStoryIds(file)

        // Compute click rate
        storyIds.forEach { storyId -> computeClickRate(date, storyId) }

        return storyIds.size.toLong()
    }

    private fun loadStoryIds(file: File): Set<Long> {
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("account_id", "device_id", "product_id", "total_clicks")
                .build(),
        )

        // Load Story-ids
        val storyIds = mutableSetOf<Long>()
        parser.use {
            for (record in parser) {
                try {
                    val storyId = record.get("product_id")?.ifEmpty { null }?.trim()
                    storyIds.add(storyId!!.toLong())
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to load story-id", ex)
                }
            }
        }

        return storyIds
    }

    private fun computeClickRate(date: LocalDate, storyId: Long) {
        val reader = dao.findByStoryIdAndTypeAndYearAndMonthAndSource(
            storyId = storyId,
            type = KpiType.READER,
            year = date.year,
            month = date.monthValue,
            source = TrafficSource.ALL,
        ).getOrNull()

        val click = dao.findByStoryIdAndTypeAndYearAndMonthAndSource(
            storyId = storyId,
            type = KpiType.CLICK,
            year = date.year,
            month = date.monthValue,
            source = TrafficSource.ALL,
        ).getOrNull()

        val value = reader?.let {
            10000 * (click?.value ?: 0L) / reader.value
        } ?: 0

        val rate = dao.findByStoryIdAndTypeAndYearAndMonthAndSource(
            storyId = storyId,
            type = KpiType.CLICK_RATE,
            year = date.year,
            month = date.monthValue,
            source = TrafficSource.ALL,
        ).getOrNull()
        if (value == 0L) {
            if (rate != null) {
                dao.delete(rate)
            }
        } else {
            if (rate != null) {
                rate.value = value
                dao.save(rate)
            } else {
                dao.save(
                    StoryKpiEntity(
                        storyId = storyId,
                        type = KpiType.CLICK_RATE,
                        year = date.year,
                        month = date.monthValue,
                        source = TrafficSource.ALL,
                        value = value,
                    )
                )
            }
        }
    }
}
