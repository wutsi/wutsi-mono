package com.wutsi.blog.client.story

data class RecommendStoryResponse(
    val stories: List<StorySummaryDto> = emptyList(),
)
