package com.wutsi.blog.kpi.domain

import com.wutsi.blog.kpi.dto.KpiType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_KPI_MONTHLY")
data class KpiMonthlyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val storyId: Long = -1,
    val type: KpiType = KpiType.NONE,
    val year: Int = 0,
    val month: Int = 0,
    val value: Long = 0,
)
