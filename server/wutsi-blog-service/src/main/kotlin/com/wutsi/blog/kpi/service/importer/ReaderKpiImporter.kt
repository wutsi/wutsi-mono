package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.service.ReaderService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ReaderKpiImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
    private val readerService: ReaderService,
) : AbstractReaderKpiImporter(storage, persister) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReaderKpiImporter::class.java)
    }

    override fun import(kpis: List<KpiReader>, date: LocalDate) {
        val map = kpis.groupBy { it.storyId }
        map.keys
            .mapNotNull { it }
            .forEach { storyId ->
                try {
                    // Persist the KPI
                    val id = storyId.toLong()
                    val value = map[storyId]?.let { kpis -> countUniqueUsers(kpis) } ?: 0
                    persister.persistStory(date, KpiType.READER, id, value)

                    // Store Reader
                    map[storyId]?.let {
                        storeReader(id, it)
                    }
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to persist story KPI - storyId=$storyId", ex)
                }
            }
    }

    private fun storeReader(storyId: Long, kpis: List<KpiReader>) {
        val userIds = kpis.mapNotNull { it.userId }.toSet()
        userIds.forEach { userId ->
            try {
                readerService.storeReader(userId = userId.toLong(), storyId = storyId)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to persist Reader - storyId=$storyId, userId=$userId", ex)
            }
        }
    }
}
