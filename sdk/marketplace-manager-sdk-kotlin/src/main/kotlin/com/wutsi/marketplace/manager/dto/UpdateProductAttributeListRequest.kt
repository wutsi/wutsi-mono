package com.wutsi.marketplace.manager.dto

import kotlin.Long
import kotlin.collections.List

public data class UpdateProductAttributeListRequest(
    public val productId: Long = 0,
    public val attributes: List<ProductAttribute> = emptyList(),
)
