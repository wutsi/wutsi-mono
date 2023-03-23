package com.wutsi.marketplace.manager.dto

import org.springframework.format.`annotation`.DateTimeFormat
import java.time.OffsetDateTime
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kotlin.Boolean
import kotlin.Int
import kotlin.String

public data class CreateDiscountRequest(
    @get:NotBlank
    @get:Size(max = 30)
    public val name: String = "",
    @get:NotBlank
    public val type: String = "",
    @get:Min(1)
    public val rate: Int = 0,
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val starts: OffsetDateTime? = null,
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val ends: OffsetDateTime? = null,
    public val allProducts: Boolean = false,
)
