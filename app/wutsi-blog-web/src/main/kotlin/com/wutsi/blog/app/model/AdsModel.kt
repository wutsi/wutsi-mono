package com.wutsi.blog.app.model

import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.app.util.NumberUtils
import java.net.URLEncoder
import java.text.DecimalFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

data class AdsModel(
    val id: String = "",
    val userId: Long = -1,
    val title: String = "",
    val imageUrl: String = "",
    val thumbnailUrl: String = "",
    val url: String? = null,
    val type: AdsType = AdsType.UNKNOWN,
    val ctaType: AdsCTAType = AdsCTAType.UNKNOWN,
    val status: AdsStatus = AdsStatus.DRAFT,
    val startDate: Date? = null,
    val endDate: Date? = null,
    val startDateText: String? = null,
    val startDateYYYYMMDD: String? = null,
    val endDateText: String? = null,
    val endDateYYYYMMDD: String? = null,
    val totalImpressions: Long = 0L,
    val totalClicks: Long = 0L,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val completedDateTime: Date? = null,
    val budget: MoneyModel = MoneyModel(),
    val currency: String = "",
    val maxImpressions: Long = 0,
    val maxDailyImpressions: Long = 0,
) {
    companion object {
        const val DEFAULT_COUNTRY_CODE = "CM"
        const val DEFAULT_CURRENCY = "XAF"
    }

    val ctaUrl: String?
        get() = url?.let { "/wclick?ads-id=$id&url=" + URLEncoder.encode(url, "utf-8") }

    val draft: Boolean
        get() = (status == AdsStatus.DRAFT)

    val published: Boolean
        get() = (status == AdsStatus.PUBLISHED)

    val running: Boolean
        get() = (status == AdsStatus.RUNNING)

    val completed: Boolean
        get() = (status == AdsStatus.COMPLETED)

    val durationDays: Long?
        get() = if (startDate == null || endDate == null || !endDate.after(startDate)) {
            null
        } else {
            TimeUnit.DAYS.convert(
                endDate.time - startDate.time,
                TimeUnit.MILLISECONDS
            )
        }

    val runDays: Long?
        get() = if (startDate == null) {
            null
        } else {
            val days = TimeUnit.DAYS.convert(
                System.currentTimeMillis() - startDate.time,
                TimeUnit.MILLISECONDS
            )
            durationDays?.let { duration -> min(duration, days) } ?: days
        }

    val totalImpressionsText: String
        get() = NumberUtils.toHumanReadable(totalImpressions)

    val totalClicksText: String
        get() = NumberUtils.toHumanReadable(totalClicks)

    val clickThroughRate: Double
        get() = if (totalImpressions == 0L) {
            0.0
        } else {
            totalClicks.toDouble() / totalImpressions.toDouble()
        }

    val clickThroughRatePercentageText: String
        get() = DecimalFormat("0.00").format(100.0 * clickThroughRate) + "%"
    val percentageComplete: Int?
        get() = if (status == AdsStatus.RUNNING || status == AdsStatus.COMPLETED) {
            val run = runDays
            val duration = durationDays
            if (run == null || duration == null || duration == 0L) {
                null
            } else {
                min(
                    100,
                    max(1, (100.0 * run.toDouble() / duration.toDouble()).toInt())
                )
            }
        } else {
            null
        }
}
