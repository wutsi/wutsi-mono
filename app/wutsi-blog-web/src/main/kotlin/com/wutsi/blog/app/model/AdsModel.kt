package com.wutsi.blog.app.model

import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import java.util.Date

data class AdsModel(
    val id: String = "",
    val userId: Long = -1,
    val title: String = "",
    val imageUrl: String? = null,
    val url: String? = null,
    val type: AdsType = AdsType.UNKNOWN,
    val ctaType: AdsCTAType = AdsCTAType.UNKNOWN,
    val status: AdsStatus = AdsStatus.DRAFT,
    val durationDays: Int = 0,
    val startDate: Date? = null,
    val endDate: Date? = null,
    val totalImpressions: Long = 0L,
    val totalClicks: Long = 0L,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val completedDateTime: Date? = null,
    val budget: MoneyModel = MoneyModel(),
    val currency: String = "",
    val maxImpressions: Long = 0,
    val maxDailyImpressions: Long = 0,
)
