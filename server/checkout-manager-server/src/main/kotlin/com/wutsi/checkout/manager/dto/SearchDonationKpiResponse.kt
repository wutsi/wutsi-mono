package com.wutsi.checkout.manager.dto

import kotlin.collections.List

public data class SearchDonationKpiResponse(
    public val kpis: List<DonationKpiSummary> = emptyList(),
)
