package com.wutsi.blog.kpi.dto

import java.time.LocalDate

data class SearchStoryKpiRequest(
    val types: List<KpiType> = emptyList(),
    val storyIds: List<Long> = emptyList(),
    val userId: Long? = null,
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,
    val dimension: Dimension = Dimension.ALL,
)
