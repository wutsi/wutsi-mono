package com.wutsi.blog.kpi.dto

data class SearchStoryKpiRequest(
    val types: List<KpiType> = emptyList(),
    val storyIds: List<Long> = emptyList(),
)
