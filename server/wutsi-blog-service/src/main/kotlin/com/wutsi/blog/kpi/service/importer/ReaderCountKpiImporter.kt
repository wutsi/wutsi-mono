package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.service.StoryService
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Update the overall reader-count for each StoryEntity
 */
@Service
class ReaderCountKpiImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
    private val storyService: StoryService,
) : AbstractReaderKpiImporter(storage, persister) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReaderCountKpiImporter::class.java)
    }

    override fun import(kpis: List<KpiReader>, date: LocalDate) {
        // Filter the stories
        val storyIds = kpis.mapNotNull {
            try {
                it.storyId?.toLong()
            } catch (ex: Exception) {
                null
            }
        }.toSet()

        // Load all the reader KPI for the stories since 2020
        val now = LocalDate.now().year
        var year = 2020
        val overallKpis = mutableListOf<KpiReader>()
        while (year < now) {
            import(storyIds, year, overallKpis)
            year++
        }

        // Group
        val map = overallKpis.groupBy { it.storyId }
        map.keys
            .mapNotNull { it }
            .forEach { storyId ->
                try {
                    val value = map[storyId]?.let { kpis -> uniqueCount(kpis) } ?: 0
                    storyService.updateReaderCount(storyId.toLong(), value)
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to persist story KPI - storyId=$storyId", ex)
                }
            }
    }

    private fun uniqueCount(kpis: List<KpiReader>): Long =
        kpis.map { it.userId ?: it.deviceId }.toSet().size.toLong()

    private fun import(storyIds: Collection<Long>, year: Int, kpis: MutableList<KpiReader>) {
        try {
            val file = downloadTrackingFile("kpi/yearly/$year/readers.csv")
            val tmp = loadKpis(file, KpiReaderCountFilter(storyIds))
            kpis.addAll(tmp)
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
        }
    }
}

class KpiReaderCountFilter(private val storyIds: Collection<Long>) : KpiReaderFilter {
    override fun accept(record: CSVRecord): Boolean =
        try {
            val storyId = record.get("product_id")?.ifEmpty { null }?.trim()?.toLong()
            storyIds.contains(storyId)
        } catch (ex: Exception) {
            false
        }
}
