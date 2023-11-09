package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

abstract class AbstractReaderKpiImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
) : AbstractImporter(storage, persister) {
    override fun getFilePath(date: LocalDate) =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/readers.csv"

    protected abstract fun import(kpis: List<KpiReader>, date: LocalDate)

    override fun import(date: LocalDate, file: File): Long {
        val kpis = loadKpis(file, CSVRecordFilterNone())
        import(kpis, date)
        return kpis.size.toLong()
    }

    protected fun loadKpis(file: File, filter: CSVRecordFilter): List<KpiReader> {
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
                if (filter.accept(record)) {
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
        }
        return kpis
    }

    protected fun countUniqueUsers(kpis: List<KpiReader>): Long =
        kpis.map { it.userId ?: it.deviceId }.toSet().size.toLong()
}

data class KpiReader(
    val userId: String?,
    val deviceId: String?,
    val storyId: String?,
    val totalReads: String?,
)
