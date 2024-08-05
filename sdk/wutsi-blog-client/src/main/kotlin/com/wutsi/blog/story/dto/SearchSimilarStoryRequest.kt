package com.wutsi.blog.story.dto

import jakarta.validation.constraints.NotEmpty

data class SearchSimilarStoryRequest(
    @get:NotEmpty val storyIds: List<Long> = emptyList(),
    val limit: Int = 20,
)
