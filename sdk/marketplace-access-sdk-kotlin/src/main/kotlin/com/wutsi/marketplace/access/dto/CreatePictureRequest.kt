package com.wutsi.marketplace.access.dto

import kotlin.Long
import kotlin.String

public data class CreatePictureRequest(
    public val productId: Long = 0,
    public val url: String = "",
)
