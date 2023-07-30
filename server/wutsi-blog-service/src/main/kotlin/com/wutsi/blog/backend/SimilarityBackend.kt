package com.wutsi.blog.backend

import com.wutsi.blog.ml.dto.SearchSimilarityRequest
import com.wutsi.blog.ml.dto.SearchSimilarityResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SimilaritiesBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.similarities.endpoint}")
    private lateinit var endpoint: String

    fun search(request: SearchSimilarityRequest): SearchSimilarityResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchSimilarityResponse::class.java).body!!
}
