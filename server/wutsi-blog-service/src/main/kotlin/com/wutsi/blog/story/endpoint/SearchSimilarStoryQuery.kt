package com.wutsi.blog.story.endpoint

import com.wutsi.blog.story.dto.SearchSimilarStoryRequest
import com.wutsi.blog.story.dto.SearchSimilarStoryResponse
import com.wutsi.blog.story.service.StorySimilarityService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping
class SearchSimilarStoryQuery(
    private val service: StorySimilarityService,
) {
    @PostMapping("/v1/stories/queries/search-similar")
    fun create(@Valid @RequestBody request: SearchSimilarStoryRequest): SearchSimilarStoryResponse =
        SearchSimilarStoryResponse(
            storyIds = service.search(request),
        )
}
