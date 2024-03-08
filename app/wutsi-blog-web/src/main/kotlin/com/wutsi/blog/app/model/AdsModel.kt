package com.wutsi.blog.app.model

import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import java.net.URLEncoder
import java.util.Date
import java.util.concurrent.TimeUnit

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

    val durationDays: Long
        get() = if (endDate == null || !endDate.after(startDate)) {
            1L
        } else {
            TimeUnit.DAYS.convert(
                endDate.time - endDate.time,
                TimeUnit.MILLISECONDS
            )
        }
}
