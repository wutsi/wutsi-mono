package com.wutsi.membership.access.dto

import kotlin.collections.List

public data class SearchAccountResponse(
    public val accounts: List<AccountSummary> = emptyList(),
)
