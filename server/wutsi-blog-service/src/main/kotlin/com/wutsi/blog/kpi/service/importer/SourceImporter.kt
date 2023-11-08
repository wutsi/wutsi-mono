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
class SourceImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
    private val storyService: StoryService,
) : AbstractImporter(storage, persister) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SourceImporter::class.java)
    }

    override fun getFilePath(date: LocalDate) =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/source.csv"

    override fun import(date: LocalDate, file: File): Long {
        val storyIds = importStoryKpi(date, file)

        val stories = storyService.searchStories(
            request = SearchStoryRequest(
                storyIds = storyIds.toList(),
                limit = storyIds.size,
            ),
        )
        val userIds = stories.map { it.userId }.toSet()
        aggregateUserKpis(date, KpiType.READ, userIds, TrafficSource.values().filter { it != TrafficSource.ALL })

        return storyIds.size.toLong()
    }

    private fun importStoryKpi(date: LocalDate, file: File): List<Long> {
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("product_id", "source", "total_reads")
                .build(),
        )

        var count = 0L
        val storyIds = mutableListOf<Long>()
        parser.use {
            for (record in parser) {
                val storyId = record.get("product_id")?.ifEmpty { null }?.trim()
                val totalReads = record.get("total_reads")?.ifEmpty { null }?.trim()
                val source = try {
                    TrafficSource.valueOf(
                        record.get("source")!!.trim()
                    )
                } catch (e: Exception) {
                    TrafficSource.UNKNOWN
                }

                try {
                    storyIds.add(storyId!!.toLong())
                    persister.persistStory(
                        date,
                        type = KpiType.READ,
                        storyId = storyId.toLong(),
                        value = totalReads!!.toLong(),
                        source = source
                    )
                    count++
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to persist story KPI - storyId=$storyId", ex)
                }
            }
        }
        return storyIds
    }
}
