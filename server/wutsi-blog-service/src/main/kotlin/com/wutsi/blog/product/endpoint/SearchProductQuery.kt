package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.dto.SearchProductResponse
import com.wutsi.blog.product.mapper.ProductMapper
import com.wutsi.blog.product.service.ProductService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchProductQuery(private val service: ProductService, private val mapper: ProductMapper) {
    @PostMapping("/v1/products/queries/search")
    fun execute(@Valid @RequestBody request: SearchProductRequest): SearchProductResponse =
        SearchProductResponse(
            products = service.search(request)
                .map { product -> mapper.toProductSummary(product) }
        )
}
