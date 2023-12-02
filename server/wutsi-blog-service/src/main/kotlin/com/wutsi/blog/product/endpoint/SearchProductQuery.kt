package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.dto.SearchProductResponse
import com.wutsi.blog.product.service.ProductService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchProductQuery(private val service: ProductService) {
    @PostMapping("/v1/products/queries/search")
    fun execute(@Valid @RequestBody request: SearchProductRequest): SearchProductResponse {
        val products = service.search(request)
        return SearchProductResponse(
            products = products.map { product ->
                ProductSummary(
                    id = product.id ?: -1,
                    storeId = product.store.id ?: "",
                    currency = product.store.currency,
                    price = product.price,
                    status = product.status,
                    title = product.title,
                    available = product.available,
                    imageUrl = product.imageUrl,
                )
            }
        )
    }
}
