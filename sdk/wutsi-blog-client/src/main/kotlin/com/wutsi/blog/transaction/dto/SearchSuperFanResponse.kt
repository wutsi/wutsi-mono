package com.wutsi.blog.transaction.dto

data class SearchSuperFanResponse(
    val superFans: List<SuperFanSummary> = emptyList(),
)
