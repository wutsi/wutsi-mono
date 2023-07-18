package com.wutsi.blog.kpi.dto

data class SearchUserKpiResponse(
    val kpis: List<UserKpi> = emptyList(),
)
