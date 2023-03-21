package com.wutsi.marketplace.access.dto

import kotlin.Int
import kotlin.Long
import kotlin.String

public data class CreateFileRequest(
    public val productId: Long = 0,
    public val name: String = "",
    public val url: String = "",
    public val contentType: String = "",
    public val contentSize: Int = 0,
)
