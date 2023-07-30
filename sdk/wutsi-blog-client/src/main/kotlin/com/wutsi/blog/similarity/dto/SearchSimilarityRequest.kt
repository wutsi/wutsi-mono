package com.wutsi.blog.similarity.dto

data class SearchSimilarityRequest(
    val ids: List<Long>,
    val similarIds: List<Long> = emptyList(),
    val limit: Int = 1000,
)
