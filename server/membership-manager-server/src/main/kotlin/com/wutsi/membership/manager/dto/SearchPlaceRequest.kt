package com.wutsi.membership.manager.dto

import kotlin.Int
import kotlin.String

public data class SearchPlaceRequest(
    public val keyword: String? = null,
    public val type: String? = null,
    public val country: String? = null,
    public val limit: Int = 100,
    public val offset: Int = 0,
)
