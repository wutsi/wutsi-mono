package com.wutsi.ml.endpoint

import com.wutsi.blog.ml.dto.SearchSimilarityRequest
import com.wutsi.blog.ml.dto.SearchSimilarityResponse
import com.wutsi.blog.ml.dto.Similarity
import com.wutsi.ml.embedding.service.TfIdfSimilarityService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchSimilarStoriesQuery(
    private val service: TfIdfSimilarityService,
) {
    @GetMapping("/v1/similarities/stories")
    fun create(@RequestBody request: SearchSimilarityRequest): SearchSimilarityResponse {
        val result = service.search(request)
        return SearchSimilarityResponse(
            similarities = result.map {
                Similarity(
                    id = it.first,
                    score = it.second,
                )
            }
        )
    }
}
