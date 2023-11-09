package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.service.StoryService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Update the overall click-count for each StoryEntity
 */
@Service
class ClickCountKpiImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
    private val storyService: StoryService,
) : AbstractClickKpiImporter(storage, persister) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ClickCountKpiImporter::class.java)
        private val START_YEAR = 2023
    }

    override fun import(kpis: List<KpiClick>, date: LocalDate) {
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
        val overallKpis = mutableListOf<KpiClick>()
        for (year in START_YEAR..now) {
            import(storyIds, year, overallKpis)
        }

        // Group
        val map = overallKpis.groupBy { it.storyId }
        map.keys
            .mapNotNull { it }
            .forEach { storyId ->
                try {
                    val value = map[storyId]?.let { kpis -> countUniqueUsers(kpis) } ?: 0
                    storyService.updateClickCount(storyId.toLong(), value)
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to persist story KPI - storyId=$storyId", ex)
                }
            }
    }

    private fun import(storyIds: Collection<Long>, year: Int, kpis: MutableList<KpiClick>) {
        val path = "kpi/yearly/$year/clicks.csv"
        try {
            val file = downloadTrackingFile(path)
            val tmp = loadKpis(file, CSVRecordFilterByProductId(storyIds))
            kpis.addAll(tmp)

            LOGGER.info("$year - Importing yearly readers from $path - ${tmp.size} imported")
        } catch (ex: Exception) {
            LOGGER.warn("Unexpected error", ex)
        }
    }
}
