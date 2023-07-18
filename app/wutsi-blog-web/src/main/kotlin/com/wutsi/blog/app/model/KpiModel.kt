package com.wutsi.blog.app.model

import com.wutsi.blog.kpi.dto.KpiType
import java.time.LocalDate

data class KpiModel(
    val id: Long? = null,
    val targetId: Long = -1,
    val type: KpiType = KpiType.NONE,
    val date: LocalDate,
    val value: Long = 0,
)
