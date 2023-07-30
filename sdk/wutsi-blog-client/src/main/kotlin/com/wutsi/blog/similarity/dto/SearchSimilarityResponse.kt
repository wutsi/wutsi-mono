package com.wutsi.blog.ml.dto

data class SearchSimilarityResponse(
    val similarities: List<Similarity> = emptyList(),
)
