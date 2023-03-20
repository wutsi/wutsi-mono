package com.wutsi.membership.access.dto

import kotlin.collections.List

public data class SearchCategoryResponse(
    public val categories: List<CategorySummary> = emptyList(),
)
