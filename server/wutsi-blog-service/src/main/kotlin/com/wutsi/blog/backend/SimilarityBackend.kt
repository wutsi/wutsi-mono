package com.wutsi.blog.backend

import com.wutsi.ml.embedding.dto.SearchSimilarStoryRequest
import com.wutsi.ml.embedding.dto.SearchSimilarStoryResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class EmbeddingBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.embedding.endpoint}")
    private lateinit var endpoint: String

    fun search(request: SearchSimilarStoryRequest): SearchSimilarStoryResponse =
        rest.postForEntity(
            "$endpoint/queries/search-similarities",
            request,
            SearchSimilarStoryResponse::class.java,
        ).body!!
}
