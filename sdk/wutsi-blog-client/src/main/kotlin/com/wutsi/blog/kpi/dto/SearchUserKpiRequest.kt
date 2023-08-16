package com.wutsi.blog.kpi.dto

data class SearchUserKpiRequest(
    val types: List<KpiType> = emptyList(),
    val userIds: List<Long> = emptyList(),
    val dimension: Dimension = Dimension.ALL,
)
