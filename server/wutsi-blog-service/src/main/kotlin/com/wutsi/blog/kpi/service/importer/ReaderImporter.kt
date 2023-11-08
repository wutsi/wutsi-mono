package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ReaderImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
) : AbstractImporter(storage, persister) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReaderImporter::class.java)
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
                    val value = map[storyId]?.let { kpis -> uniqueCount(kpis) } ?: 0
                    persister.persistStory(date, KpiType.READER, storyId.toLong(), value)
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to persist story KPI - storyId=$storyId", ex)
                }
            }

        return kpis.size.toLong()
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
