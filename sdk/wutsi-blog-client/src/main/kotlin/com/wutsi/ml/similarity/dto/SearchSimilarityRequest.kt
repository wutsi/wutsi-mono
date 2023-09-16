package com.wutsi.ml.similarity.dto

data class SearchSimilarityRequest(
    val itemIds: List<Long> = emptyList(),
    val model: SimilarityModelType = SimilarityModelType.UNKNOWN,
    val limit: Int = 1000,
)
