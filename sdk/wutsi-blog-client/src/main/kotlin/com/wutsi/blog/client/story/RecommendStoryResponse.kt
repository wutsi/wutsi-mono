package com.wutsi.blog.client.story

import com.wutsi.blog.story.dto.StorySummary

data class RecommendStoryResponse(
    val stories: List<StorySummary> = emptyList(),
)
