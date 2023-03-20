package com.wutsi.membership.access.dto

import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List

public data class SearchCategoryRequest(
    public val categoryIds: List<Long> = emptyList(),
    public val keyword: String? = null,
    public val limit: Int = 100,
    public val offset: Int = 0,
)
