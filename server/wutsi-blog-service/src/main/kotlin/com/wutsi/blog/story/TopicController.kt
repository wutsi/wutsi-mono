package com.wutsi.blog.story

import com.wutsi.blog.client.story.SearchTopicResponse
import com.wutsi.blog.story.mapper.TopicMapper
import com.wutsi.blog.story.service.TopicService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TopicController(
    private val service: TopicService,
    private val mapper: TopicMapper,
) {
    @GetMapping("/v1/topics")
    fun all(): SearchTopicResponse {
        val topics = service.all()
        return SearchTopicResponse(
            topics = topics.map { mapper.toTopicDto(it) },
        )
    }
}
