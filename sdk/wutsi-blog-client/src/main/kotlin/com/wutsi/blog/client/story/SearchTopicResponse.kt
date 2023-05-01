package com.wutsi.blog.client.story

data class SearchTopicResponse(
    val topics: List<TopicDto> = emptyList(),
)
