package com.wutsi.blog.app.backend

import com.wutsi.blog.product.dto.SearchDiscountRequest
import com.wutsi.blog.product.dto.SearchDiscountResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class DiscountBackend(
    private val rest: RestTemplate,
    @Value("\${wutsi.application.backend.discount.endpoint}") private val endpoint: String,
) {
    fun search(request: SearchDiscountRequest): SearchDiscountResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchDiscountResponse::class.java).body!!
}
