package com.wutsi.blog.backend

import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.similarity.dto.SearchSimilarityResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Deprecated("ML disabled for the moment")
@Service
class SimilarityBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.similarities.endpoint}")
    private lateinit var endpoint: String

    fun search(request: SearchSimilarityRequest): SearchSimilarityResponse =
        rest.postForEntity(
            "$endpoint/queries/search",
            request,
            SearchSimilarityResponse::class.java,
        ).body!!
}
