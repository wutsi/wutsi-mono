package com.wutsi.blog.app.model

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import java.time.LocalDate

data class KpiModel(
	val id: Long? = null,
	val targetId: Long = -1,
	val type: KpiType = KpiType.NONE,
	val date: LocalDate,
	val value: Double = 0.0,
	val source: TrafficSource = TrafficSource.ALL,
) {
	val valueInt: Int
		get() = value.toInt()

	val valuePercent: String
		get() = String.format("%.2f", value) + "%"
}
