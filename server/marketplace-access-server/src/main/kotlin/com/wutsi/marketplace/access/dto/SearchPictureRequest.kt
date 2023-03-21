package com.wutsi.marketplace.access.dto

import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List

public data class SearchPictureRequest(
    public val pictureIds: List<Long> = emptyList(),
    public val productIds: List<Long> = emptyList(),
    public val pictureUrls: List<String> = emptyList(),
    public val limit: Int = 100,
    public val offset: Int = 0,
)
