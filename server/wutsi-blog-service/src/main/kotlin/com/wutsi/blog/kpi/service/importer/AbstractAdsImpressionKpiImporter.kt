package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import java.io.File
import java.time.LocalDate

abstract class AbstractAdsImpressionKpiImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
) : AbstractImporter(storage, persister) {
    protected abstract fun persist(date: LocalDate, id: String, impressions: Long)

    protected fun importKpis(date: LocalDate, file: File): List<String> {
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("campaign", "total_impressions")
                .build(),
        )

        val ids = mutableSetOf<String>()
        parser.use {
            for (record in parser) {
                val adsId = record.get("campaign")?.ifEmpty { null }?.trim()
                if (adsId != null) {
                    try {
                        val totalImpressions = record.get("total_impressions")?.ifEmpty { null }?.toLong() ?: 0

                        persist(date, adsId, totalImpressions)
                        ids.add(adsId)
                    } catch (ex: Exception) {
                        LoggerFactory.getLogger(this.javaClass).warn("Unable to persist Ads KPI - adsId=$adsId", ex)
                    }
                }
            }
        }
        return ids.toList()
    }
}
