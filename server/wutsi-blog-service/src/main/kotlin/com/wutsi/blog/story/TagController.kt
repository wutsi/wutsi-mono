package com.wutsi.blog.story

import com.wutsi.blog.story.dto.SearchTagResponse
import com.wutsi.blog.story.mapper.TagMapper
import com.wutsi.blog.story.service.TagService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TagController(
    private val service: TagService,
    private val mapper: TagMapper,
) {
    @GetMapping("/v1/tags")
    fun search(
        @RequestParam query: String,
        @RequestParam(required = false, defaultValue = "20") limit: Int = 20,
    ): SearchTagResponse {
        val tags = service.search(query)
        return SearchTagResponse(
            tags = tags.map { mapper.toTagDto(it) }.take(limit),
        )
    }
}
