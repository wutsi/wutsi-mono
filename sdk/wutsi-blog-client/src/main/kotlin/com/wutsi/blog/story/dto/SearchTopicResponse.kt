package com.wutsi.blog.story.dto

data class SearchTopicResponse(
    val topics: List<Topic> = emptyList(),
)
