package com.wutsi.blog.kpi.dto

data class StoryKpi(
    val id: Long? = null,
    val storyId: Long = -1,
    val type: KpiType = KpiType.NONE,
    val source: TrafficSource = TrafficSource.ALL,
    val year: Int = 0,
    val month: Int = 0,
    val value: Long = 0,
)
