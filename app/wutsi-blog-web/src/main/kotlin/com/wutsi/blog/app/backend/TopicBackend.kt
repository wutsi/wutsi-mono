package com.wutsi.blog.app.backend

import com.wutsi.blog.story.dto.SearchTopicResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class TopicBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.topic.endpoint}")
    private lateinit var endpoint: String

    fun all(): SearchTopicResponse {
        return rest.getForEntity(endpoint, SearchTopicResponse::class.java).body!!
    }
}
