package com.wutsi.checkout.access.dto

import org.springframework.format.`annotation`.DateTimeFormat
import java.time.LocalDate
import kotlin.Long

public data class DonationKpiSummary(
    @get:DateTimeFormat(pattern = "yyyy-MM-dd")
    public val date: LocalDate = LocalDate.now(),
    public val totalDonations: Long = 0,
    public val totalValue: Long = 0,
)
