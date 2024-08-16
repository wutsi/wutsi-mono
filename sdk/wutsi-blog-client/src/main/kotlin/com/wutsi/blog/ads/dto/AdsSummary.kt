package com.wutsi.blog.ads.dto

import java.util.Date

data class AdsSummary(
    val id: String = "",
    val title: String = "",
    val imageUrl: String? = null,
    val status: AdsStatus = AdsStatus.DRAFT,
    val startDate: Date? = null,
    val endDate: Date? = null,
    val totalImpressions: Long = 0L,
    val totalClicks: Long = 0L,
    val budget: Long = 0L,
    val dailyBudget: Long = 0L,
    val durationDays: Int = 0,
    val currency: String = "",
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val type: AdsType = AdsType.UNKNOWN,
    val ctaType: AdsCTAType = AdsCTAType.UNKNOWN,
    val url: String? = null,
    val transactionId: String? = null,
    val categoryId: String? = null,
)
