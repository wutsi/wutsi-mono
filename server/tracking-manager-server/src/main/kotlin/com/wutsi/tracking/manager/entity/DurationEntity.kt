package com.wutsi.tracking.manager.entity

data class DurationEntity(
    val correlationId: String = "",
    val productId: String = "",
    val totalMinutes: Long = 0,
)
