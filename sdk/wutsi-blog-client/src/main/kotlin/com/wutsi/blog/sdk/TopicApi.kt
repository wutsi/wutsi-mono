package com.wutsi.blog.sdk

import com.wutsi.blog.client.story.SearchTopicResponse

interface TopicApi {
    fun all(): SearchTopicResponse
}
