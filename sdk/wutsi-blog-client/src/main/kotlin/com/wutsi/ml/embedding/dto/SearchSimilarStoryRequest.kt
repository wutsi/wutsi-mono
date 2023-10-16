package com.wutsi.ml.embedding.dto

@Deprecated("")
data class SearchSimilarStoryRequest(
    val storyIds: List<Long> = emptyList(),
    val limit: Int = 1000,
)
