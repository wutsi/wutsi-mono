package com.wutsi.tracking.manager.entity

data class SourceEntity(
    val productId: String = "",
    val source: TrafficSource = TrafficSource.UNKNOWN,
    val totalReads: Long = 0,
)
