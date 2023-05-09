package com.wutsi.blog.client.story

data class RecommendStoryRequest(
    val storyId: Long? = null,
    val limit: Int = 20,
    val context: SearchStoryContext = SearchStoryContext(),
)
