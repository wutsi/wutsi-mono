package com.wutsi.ml.embedding.endpoint

import com.wutsi.blog.similarity.dto.SearchSimilarityRequest
import com.wutsi.blog.similarity.dto.SearchSimilarityResponse
import com.wutsi.blog.similarity.dto.Similarity
import com.wutsi.ml.embedding.service.TfIdfSimilarityService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchSimilarStoriesQuery(
    private val service: TfIdfSimilarityService,
) {
    @PostMapping("/v1/similarities/queries/search")
    fun search(@RequestBody request: SearchSimilarityRequest): SearchSimilarityResponse {
        val result = service.search(request)
        return SearchSimilarityResponse(
            similarities = result.map {
                Similarity(
                    id = it.first,
                    score = it.second,
                )
            },
        )
    }
}
