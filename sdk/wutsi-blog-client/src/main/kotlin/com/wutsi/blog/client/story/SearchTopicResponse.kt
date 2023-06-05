package com.wutsi.blog.client.story

import com.wutsi.blog.story.dto.Topic

data class SearchTopicResponse(
    val topics: List<Topic> = emptyList(),
)
