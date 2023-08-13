package com.wutsi.ml.embedding.endpoint

import com.wutsi.ml.embedding.dto.SearchSimilarStoryRequest
import com.wutsi.ml.embedding.dto.SearchSimilarStoryResponse
import com.wutsi.ml.embedding.dto.Story
import com.wutsi.ml.embedding.service.TfIdfEmbeddingService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchSimilarStoriesQuery(
    private val service: TfIdfEmbeddingService,
) {
    @PostMapping("/v1/embeddings/queries/search-similarities")
    fun search(@RequestBody request: SearchSimilarStoryRequest): SearchSimilarStoryResponse {
        val result = service.search(request)
        return SearchSimilarStoryResponse(
            stories = result.map {
                Story(
                    id = it.first,
                    score = it.second,
                )
            },
        )
    }
}
