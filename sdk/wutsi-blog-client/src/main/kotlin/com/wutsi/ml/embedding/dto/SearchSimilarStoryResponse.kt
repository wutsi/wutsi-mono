package com.wutsi.ml.embedding.dto

data class SearchSimilarStoryResponse(
    val stories: List<Story> = emptyList(),
)
