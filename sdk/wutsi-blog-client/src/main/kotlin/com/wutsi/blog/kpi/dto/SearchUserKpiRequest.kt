package com.wutsi.blog.kpi.dto

import java.time.LocalDate

data class SearchUserKpiRequest(
    val types: List<KpiType> = emptyList(),
    val userIds: List<Long> = emptyList(),
    val categoryId: Long? = null,
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,
    val dimension: Dimension = Dimension.ALL,
)
