package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.SearchOfferRequest
import com.wutsi.blog.product.dto.SearchOfferResponse
import com.wutsi.blog.product.service.OfferService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchOfferQuery(private val service: OfferService) {
    @PostMapping("/v1/offers/queries/search")
    fun execute(@RequestBody @Valid request: SearchOfferRequest): SearchOfferResponse =
        SearchOfferResponse(
            offers = service.search(request)
        )
}
