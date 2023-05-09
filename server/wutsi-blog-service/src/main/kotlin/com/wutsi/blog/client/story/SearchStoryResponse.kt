package com.wutsi.blog.client.story

data class SearchStoryResponse(
    val stories: List<StorySummaryDto> = emptyList(),
)
