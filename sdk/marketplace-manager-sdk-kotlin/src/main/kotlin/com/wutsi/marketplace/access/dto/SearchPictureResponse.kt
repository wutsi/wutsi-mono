package com.wutsi.marketplace.access.dto

import kotlin.collections.List

public data class SearchPictureResponse(
    public val pictures: List<PictureSummary> = emptyList(),
)
