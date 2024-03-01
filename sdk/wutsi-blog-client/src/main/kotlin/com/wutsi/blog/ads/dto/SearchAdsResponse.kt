package com.wutsi.blog.ads.dto

data class SearchAdsResponse(
    val ads: List<AdsSummary> = emptyList(),
)
