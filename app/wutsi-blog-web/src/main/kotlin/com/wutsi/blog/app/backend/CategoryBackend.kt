package com.wutsi.blog.app.backend

import com.wutsi.blog.product.dto.SearchCategoryRequest
import com.wutsi.blog.product.dto.SearchCategoryResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CategoryBackend(
    private val rest: RestTemplate,
    @Value("\${wutsi.application.backend.category.endpoint}") private var endpoint: String,
) {
    fun search(request: SearchCategoryRequest): SearchCategoryResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchCategoryResponse::class.java).body!!
}
