package com.wutsi.marketplace.manager.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import kotlin.Long
import kotlin.collections.List

public data class UpdateProductAttributeListRequest(
    public val productId: Long = 0,
    @get:NotNull
    @get:NotEmpty
    public val attributes: List<ProductAttribute> = emptyList(),
)
