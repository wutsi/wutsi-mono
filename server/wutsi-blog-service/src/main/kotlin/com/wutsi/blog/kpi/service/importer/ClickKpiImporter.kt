package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ClickKpiImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
) : AbstractClickKpiImporter(storage, persister) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ClickKpiImporter::class.java)
    }

    override fun import(kpis: List<KpiClick>, date: LocalDate) {
        val map = kpis.groupBy { it.storyId }
        map.keys
            .mapNotNull { it }
            .forEach { storyId ->
                try {
                    val value = map[storyId]?.let { kpis -> countUniqueUsers(kpis) } ?: 0
                    persister.persistStory(date, KpiType.CLICK, storyId.toLong(), value)
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to persist story KPI - storyId=$storyId", ex)
                }
            }
    }
}
