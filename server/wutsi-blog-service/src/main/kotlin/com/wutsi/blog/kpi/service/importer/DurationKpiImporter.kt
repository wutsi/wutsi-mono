package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.StoryService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class DurationKpiImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
    private val storyService: StoryService,
) : AbstractImporter(storage, persister) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DurationKpiImporter::class.java)
    }

    override fun getFilePath(date: LocalDate) =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/durations.csv"

    override fun import(date: LocalDate, file: File): Long {
        // Import KPIs
        val kpis = importStoryKpis(date, file)

        // Aggregate user KPIs
        val storyIds = kpis.mapNotNull {
            try {
                it.storyId?.toLong()
            } catch (ex: Exception) {
                null
            }
        }.toSet()
        val stories = storyService.searchStories(
            request = SearchStoryRequest(
                storyIds = storyIds.toList(),
                limit = storyIds.size,
            ),
        )
        val userIds = stories.map { it.userId }.toSet()
        aggregateUserKpis(date, KpiType.DURATION, userIds, listOf(TrafficSource.ALL))

        return kpis.size.toLong()
    }

    private fun importStoryKpis(date: LocalDate, file: File): Collection<KpiDuration> {
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("correlation_id", "product_id", "total_seconds")
                .build(),
        )

        val kpis: MutableList<KpiDuration> = mutableListOf()
        parser.use {
            for (record in parser) {
                kpis.add(
                    KpiDuration(
                        correlationId = record.get("correlation_id")?.ifEmpty { null }?.trim(),
                        storyId = record.get("product_id")?.ifEmpty { null }?.trim(),
                        totalSeconds = record.get("total_seconds")?.ifEmpty { null }?.trim(),
                    )
                )
            }
        }

        val map = kpis.groupBy { it.storyId }
        map.keys
            .mapNotNull { it }
            .forEach { storyId ->
                try {
                    val id = storyId.toLong()
                    val value = map[storyId]?.let { kpis -> sum(kpis) } ?: 0
                    persister.persistStory(date, KpiType.DURATION, id, value)
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to persist story KPI - storyId=$storyId", ex)
                }
            }

        return kpis
    }

    private fun sum(kpis: List<KpiDuration>): Long =
        kpis.map {
            try {
                it.totalSeconds?.toLong() ?: 0
            } catch (ex: Exception) {
                0L
            }
        }.sum()
}

private data class KpiDuration(
    val correlationId: String?,
    val storyId: String?,
    val totalSeconds: String?,
)
