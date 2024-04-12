package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.ads.service.AdsService
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.util.DateUtils
import org.springframework.stereotype.Service
import java.io.File
import java.time.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

@Service
class AdsImpressionTodayKpiImporter(
    private val adsService: AdsService,
    private val clock: Clock,
    storage: TrackingStorageService,
    persister: KpiPersister,
) : AbstractAdsImpressionKpiImporter(storage, persister) {
    override fun getFilePath(date: LocalDate) =
        "kpi/daily/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/ads_impressions.csv"

    override fun persist(date: LocalDate, id: String, impressions: Long) {
        adsService.onTodayImpressionImported(id, impressions)
    }

    override fun import(date: LocalDate, file: File): Long {
        return if (DateUtils.toLocalDate(Date(clock.millis())) == date) {
            importKpis(date, file).size.toLong()
        } else {
            0
        }
    }
}
