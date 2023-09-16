package com.wutsi.ml.similarity.endpoint

import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.similarity.dto.SearchSimilarityResponse
import com.wutsi.ml.similarity.model.SimilarityModelFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchSimilarityQuery(
    private val factory: SimilarityModelFactory,
) {
    @PostMapping("/v1/similarities/queries/search")
    fun search(@RequestBody request: SearchSimilarityRequest): SearchSimilarityResponse =
        factory.get(request.model).search(request)
}
