package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.ads.service.AdsService
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
class AdsClickKpiImporter(
    private val adsService: AdsService,
    storage: TrackingStorageService,
    persister: KpiPersister,
) : AbstractImporter(storage, persister) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AdsClickKpiImporter::class.java)
    }

    override fun getFilePath(date: LocalDate) =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/ads_clicks.csv"

    override fun import(date: LocalDate, file: File): Long {
        val ids = importKpis(date, file)
        if (ids.isNotEmpty()) {
            adsService.findByIds(ids).forEach { ad ->
                adsService.onKpiImported(ad)
            }
        }
        return ids.size.toLong()
    }

    private fun importKpis(date: LocalDate, file: File): List<String> {
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("campaign", "total_clicks")
                .build(),
        )

        val ids = mutableSetOf<String>()
        parser.use {
            for (record in parser) {
                val adsId = record.get("campaign")?.ifEmpty { null }?.trim()
                if (adsId != null) {
                    val total = record.get("total_clicks")?.ifEmpty { null }?.trim()

                    try {
                        persister.persistAds(
                            date,
                            type = KpiType.CLICK,
                            adsId = adsId,
                            value = total!!.toLong(),
                        )
                        ids.add(adsId)
                    } catch (ex: Exception) {
                        LOGGER.warn("Unable to persist Ads KPI - adsId=$adsId", ex)
                    }
                }
            }
        }
        return ids.toList()
    }
}
