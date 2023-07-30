package com.wutsi.blog.ml.dto

data class SearchSimilarityRequest(
    val id: Long,
    val limit: Int = 1000,
)
