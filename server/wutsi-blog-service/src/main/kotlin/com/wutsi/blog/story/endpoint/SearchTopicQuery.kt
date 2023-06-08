package com.wutsi.blog.story.endpoint

import com.wutsi.blog.story.dto.SearchTopicResponse
import com.wutsi.blog.story.mapper.TopicMapper
import com.wutsi.blog.story.service.TopicService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(("/v1/topics/queries/search"))
class SearchTopicQuery(
    private val service: TopicService,
    private val mapper: TopicMapper,
) {
    @GetMapping
    fun all(): SearchTopicResponse {
        val topics = service.all()
        return SearchTopicResponse(
            topics = topics.map { mapper.toTopicDto(it) },
        )
    }
}
