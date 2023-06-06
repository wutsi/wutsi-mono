package com.wutsi.blog.client.story

import com.wutsi.blog.story.dto.StorySummary

@Deprecated("")
data class RecommendStoryResponse(
    val stories: List<StorySummary> = emptyList(),
)
