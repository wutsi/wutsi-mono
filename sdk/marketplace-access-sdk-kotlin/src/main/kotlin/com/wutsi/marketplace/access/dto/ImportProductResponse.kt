package com.wutsi.marketplace.access.dto

import kotlin.Boolean
import kotlin.collections.List

public data class ImportProductResponse(
    public val success: Boolean = false,
    public val errors: List<ImportError> = emptyList(),
)
