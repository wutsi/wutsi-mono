package com.wutsi.marketplace.access.dto

import kotlin.Int
import kotlin.String

public data class ImportError(
    public val row: Int = 0,
    public val colum: Int = 0,
    public val code: String = "",
    public val description: String = "",
)
