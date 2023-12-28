package com.wutsi.blog.app.backend

import com.wutsi.blog.product.dto.SearchOfferRequest
import com.wutsi.blog.product.dto.SearchOfferResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class OfferBackend(
    private val rest: RestTemplate,
    @Value("\${wutsi.application.backend.offer.endpoint}") private val endpoint: String,
) {
    fun search(request: SearchOfferRequest): SearchOfferResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchOfferResponse::class.java).body!!
}
