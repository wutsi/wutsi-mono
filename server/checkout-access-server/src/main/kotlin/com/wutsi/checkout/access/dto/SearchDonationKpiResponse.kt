package com.wutsi.checkout.access.dto

import kotlin.collections.List

public data class SearchDonationKpiResponse(
    public val kpis: List<DonationKpiSummary> = emptyList(),
)
