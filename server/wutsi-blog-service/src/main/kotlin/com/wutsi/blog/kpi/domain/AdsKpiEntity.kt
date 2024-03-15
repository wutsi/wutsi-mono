package com.wutsi.blog.kpi.domain

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_ADS_KPI")
data class AdsKpiEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val adsId: String = "",
    val type: KpiType = KpiType.NONE,
    val source: TrafficSource = TrafficSource.ALL,
    val year: Int = 0,
    val month: Int = 0,
    var value: Long = 0,
)
