package com.wutsi.checkout.access.dto

import java.time.LocalDate
import kotlin.Long

public data class DonationKpiSummary(
    public val date: LocalDate = LocalDate.now(),
    public val totalDonations: Long = 0,
    public val totalValue: Long = 0,
)
