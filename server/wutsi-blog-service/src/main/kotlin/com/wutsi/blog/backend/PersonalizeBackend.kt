package com.wutsi.blog.backend

import com.wutsi.ml.personalize.dto.RecommendStoryRequest
import com.wutsi.ml.personalize.dto.RecommendStoryResponse
import com.wutsi.ml.personalize.dto.SortStoryRequest
import com.wutsi.ml.personalize.dto.SortStoryResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class PersonalizeBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.personalize.endpoint}")
    private lateinit var endpoint: String

    fun recommend(request: RecommendStoryRequest): RecommendStoryResponse =
        rest.postForEntity(
            "$endpoint/queries/recommend",
            request,
            RecommendStoryResponse::class.java,
        ).body!!

    fun sort(request: SortStoryRequest): SortStoryResponse =
        rest.postForEntity(
            "$endpoint/queries/sort",
            request,
            SortStoryResponse::class.java,
        ).body!!
}
