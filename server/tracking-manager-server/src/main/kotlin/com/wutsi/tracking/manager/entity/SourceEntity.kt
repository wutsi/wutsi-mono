package com.wutsi.tracking.manager.entity

import com.wutsi.blog.kpi.dto.TrafficSource

data class SourceEntity(
    val productId: String = "",
    val source: TrafficSource = TrafficSource.UNKNOWN,
    val totalReads: Long = 0,
)
