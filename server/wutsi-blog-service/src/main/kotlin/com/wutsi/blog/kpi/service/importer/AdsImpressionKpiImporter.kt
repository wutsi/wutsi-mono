package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.ads.service.AdsService
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class AdsImpressionKpiImporter(
    private val adsService: AdsService,
    storage: TrackingStorageService,
    persister: KpiPersister,
) : AbstractAdsImpressionKpiImporter(storage, persister) {
    override fun getFilePath(date: LocalDate) =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/ads_impressions.csv"

    override fun persist(date: LocalDate, id: String, impressions: Long) {
        persister.persistAds(
            date,
            type = KpiType.IMPRESSION,
            adsId = id,
            value = impressions,
        )
    }

    override fun import(date: LocalDate, file: File): Long {
        val ids = importKpis(date, file)
        if (ids.isNotEmpty()) {
            adsService.findByIds(ids).forEach { ad ->
                adsService.onKpiImported(ad)
            }
        }
        return ids.size.toLong()
    }
}
