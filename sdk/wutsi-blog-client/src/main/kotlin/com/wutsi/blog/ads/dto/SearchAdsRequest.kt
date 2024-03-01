package com.wutsi.blog.ads.dto

import com.wutsi.blog.SortOrder
import java.util.Date

data class SearchAdsRequest(
    val userId: Long? = null,
    val type: List<AdsType> = emptyList(),
    val status: List<AdsStatus> = emptyList(),
    val startDateFrom: Date? = null,
    val startDateTo: Date? = null,
    val endDateFrom: Date? = null,
    val endDateTo: Date? = null,
    val limit: Int = 20,
    val offset: Int = 0,
    val sortBy: AdsSortStrategy = AdsSortStrategy.NONE,
    val sortOrder: SortOrder? = null,
    val impressionContext: AdsImpressionContext? = null,
)
