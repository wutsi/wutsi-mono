package com.wutsi.blog.story.dto

data class SearchStoryResponse(
    val stories: List<StorySummary> = emptyList(),
)
