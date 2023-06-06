package com.wutsi.blog.client.story

import com.wutsi.blog.story.dto.SearchStoryContext

@Deprecated("")
data class RecommendStoryRequest(
    val storyId: Long? = null,
    val limit: Int = 20,
    val context: SearchStoryContext = SearchStoryContext(),
)
