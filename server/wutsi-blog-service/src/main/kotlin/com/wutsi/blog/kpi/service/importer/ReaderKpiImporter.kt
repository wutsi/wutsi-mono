package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.service.ReaderService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ReaderKpiImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
    private val readerService: ReaderService,
) : AbstractImporter(storage, persister) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReaderKpiImporter::class.java)
    }

    override fun getFilePath(date: LocalDate) =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/readers.csv"

    override fun import(date: LocalDate, file: File): Long {
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("account_id", "device_id", "product_id", "total_reads")
                .build(),
        )

        val kpis: MutableList<KpiReader> = mutableListOf()
        parser.use {
            for (record in parser) {
                kpis.add(
                    KpiReader(
                        userId = record.get("account_id")?.ifEmpty { null }?.trim(),
                        deviceId = record.get("device_id")?.ifEmpty { null }?.trim(),
                        storyId = record.get("product_id")?.ifEmpty { null }?.trim(),
                        totalReads = record.get("total_reads")?.ifEmpty { null }?.trim(),
                    )
                )
            }
        }

        val map = kpis.groupBy { it.storyId }
        map.keys
            .mapNotNull { it }
            .forEach { storyId ->
                try {
                    // Persist the KPI
                    val id = storyId.toLong()
                    val value = map[storyId]?.let { kpis -> uniqueCount(kpis) } ?: 0
                    persister.persistStory(date, KpiType.READER, id, value)

                    // Store Reader
                    map[storyId]?.let {
                        storeReader(id, it)
                    }
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to persist story KPI - storyId=$storyId", ex)
                }
            }

        return kpis.size.toLong()
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

    private fun uniqueCount(kpis: List<KpiReader>): Long =
        kpis.map { it.userId ?: it.deviceId }.toSet().size.toLong()
}

private data class KpiReader(
    val userId: String?,
    val deviceId: String?,
    val storyId: String?,
    val totalReads: String?,
)
