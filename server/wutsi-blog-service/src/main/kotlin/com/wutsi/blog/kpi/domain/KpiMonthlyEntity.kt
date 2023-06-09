package com.wutsi.blog.kpi.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_READ_MONTHLY_KPI")
data class MonthlyKpiEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val storyId: Long = -1,
    val year: Int = 0,
    val month: Int = 0,
    val value: Long = 0,
)
