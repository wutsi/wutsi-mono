package com.wutsi.blog.story.dto

data class SearchSimilarStoryResponse(
    val storyIds: List<Long> = emptyList(),
)
