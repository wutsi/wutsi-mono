package com.wutsi.marketplace.access.dto

import javax.validation.constraints.NotBlank
import kotlin.Int
import kotlin.Long
import kotlin.String

public data class CreateProductRequest(
    public val storeId: Long = 0,
    public val pictureUrl: String? = null,
    public val categoryId: Long? = null,
    @get:NotBlank
    public val title: String = "",
    public val summary: String? = null,
    public val price: Long? = null,
    public val quantity: Int? = null,
    @get:NotBlank
    public val type: String = "PHYSICAL_PRODUCT",
)
