package com.wutsi.checkout.manager.dto

import java.time.LocalDate
import kotlin.Boolean
import kotlin.Long

public data class SearchDonationKpiRequest(
    public val businessId: Long? = null,
    public val fromDate: LocalDate? = null,
    public val toDate: LocalDate? = null,
    public val aggregate: Boolean = false,
)
