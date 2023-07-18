package com.wutsi.blog.kpi.dto

data class SearchStoryKpiResponse(
    val kpis: List<StoryKpi> = emptyList(),
)
