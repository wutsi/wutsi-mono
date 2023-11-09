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
class ClickKpiImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
) : AbstractImporter(storage, persister) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ClickKpiImporter::class.java)
    }

    override fun getFilePath(date: LocalDate) =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/clicks.csv"

    override fun import(date: LocalDate, file: File): Long {
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("account_id", "device_id", "product_id", "total_clicks")
                .build(),
        )

        // Load kpis
        val kpis: MutableList<KpiClick> = mutableListOf()
        parser.use {
            for (record in parser) {
                kpis.add(
                    KpiClick(
                        userId = record.get("account_id")?.ifEmpty { null }?.trim(),
                        deviceId = record.get("device_id")?.ifEmpty { null }?.trim(),
                        storyId = record.get("product_id")?.ifEmpty { null }?.trim(),
                        totalClicks = record.get("total_clicks")?.ifEmpty { null }?.trim(),
                    )
                )
            }
        }

        // Store kpis
        val map = kpis.groupBy { it.storyId }
        map.keys
            .mapNotNull { it }
            .forEach { storyId ->
                try {
                    val value = map[storyId]?.let { kpis -> uniqueCount(kpis) } ?: 0
                    persister.persistStory(date, KpiType.CLICK, storyId.toLong(), value)
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to persist story KPI - storyId=$storyId", ex)
                }
            }

        return kpis.size.toLong()
    }

    private fun uniqueCount(kpis: List<KpiClick>): Long =
        kpis.map { it.userId ?: it.deviceId }.toSet().size.toLong()
}

private data class KpiClick(
    val userId: String?,
    val deviceId: String?,
    val storyId: String?,
    val totalClicks: String?
)
