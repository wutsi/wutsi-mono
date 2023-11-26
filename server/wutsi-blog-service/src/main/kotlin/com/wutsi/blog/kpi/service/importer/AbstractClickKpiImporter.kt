package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

abstract class AbstractClickKpiImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
) : AbstractImporter(storage, persister) {
    protected abstract fun import(kpis: List<KpiClick>, date: LocalDate)

    override fun getFilePath(date: LocalDate) =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/clicks.csv"

    override fun import(date: LocalDate, file: File): Long {
        val kpis = loadKpis(file, CSVRecordFilterNone())
        import(kpis, date)

        return kpis.size.toLong()
    }

    protected fun loadKpis(file: File, filter: CSVRecordFilter): List<KpiClick> {
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
                if (filter.accept(record)) {
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
        }
        return kpis
    }

    protected fun countUniqueUsers(kpis: List<KpiClick>): Long {
        // When device-id AND user-id are null, we consider each click or 1 user
        // This was caused by a bug where neither the user-id nor device-id was set
        val result1 = kpis.filter { it.userId.isNullOrEmpty() && it.deviceId.isNullOrEmpty() }
            .sumOf { it.totalClicks?.toLong() ?: 0 }

        // When device-id OR user-id are not null
        val result2 = kpis.mapNotNull { it.userId ?: it.deviceId }
            .toSet()
            .size

        return result1 + result2
    }
}

data class KpiClick(
    val userId: String?,
    val deviceId: String?,
    val storyId: String?,
    val totalClicks: String?
)
