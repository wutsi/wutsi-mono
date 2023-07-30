package com.wutsi.blog.similarity.dto

data class SearchSimilarityResponse(
    val similarities: List<Similarity> = emptyList(),
)
