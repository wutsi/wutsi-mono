package com.wutsi.checkout.manager.dto

import java.time.LocalDate
import kotlin.Long

public data class SalesKpiSummary(
    public val date: LocalDate = LocalDate.now(),
    public val totalOrders: Long = 0,
    public val totalUnits: Long = 0,
    public val totalValue: Long = 0,
    public val totalViews: Long = 0,
)
