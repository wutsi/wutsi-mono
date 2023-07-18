package com.wutsi.blog.kpi.dto

data class UserKpi(
    val id: Long? = null,
    val userId: Long = -1,
    val type: KpiType = KpiType.NONE,
    val year: Int = 0,
    val month: Int = 0,
    val value: Long = 0,
)
