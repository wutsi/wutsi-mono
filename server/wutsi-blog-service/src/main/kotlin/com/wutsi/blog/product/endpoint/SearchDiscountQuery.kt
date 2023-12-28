package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.SearchDiscountRequest
import com.wutsi.blog.product.dto.SearchDiscountResponse
import com.wutsi.blog.product.service.DiscountService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchDiscountQuery(private val service: DiscountService) {
    @PostMapping("/v1/discounts/queries/search")
    fun execute(@RequestBody @Valid request: SearchDiscountRequest): SearchDiscountResponse =
        SearchDiscountResponse(
            discounts = service.search(request)
        )
}
