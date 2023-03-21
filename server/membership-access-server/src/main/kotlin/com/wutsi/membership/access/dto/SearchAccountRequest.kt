package com.wutsi.membership.access.dto

import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List

public data class SearchAccountRequest(
    public val accountIds: List<Long> = emptyList(),
    public val phoneNumber: String? = null,
    public val status: String? = null,
    public val business: Boolean? = null,
    public val store: Boolean? = null,
    public val cityId: Long? = null,
    public val limit: Int = 100,
    public val offset: Int = 0,
)
