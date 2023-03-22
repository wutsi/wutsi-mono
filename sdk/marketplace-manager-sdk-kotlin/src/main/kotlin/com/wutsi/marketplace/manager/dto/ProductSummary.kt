package com.wutsi.marketplace.manager.dto

import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String

public data class ProductSummary(
    public val id: Long = 0,
    public val storeId: Long = 0,
    public val thumbnailUrl: String? = null,
    public val title: String = "",
    public val summary: String? = null,
    public val price: Long? = null,
    public val categoryId: Long? = null,
    public val currency: String = "",
    public val quantity: Int? = null,
    public val status: String = "",
    public val type: String = "",
    public val event: Event? = null,
    public val outOfStock: Boolean = false,
    public val url: String = "",
)
