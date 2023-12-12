package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.GetProductResponse
import com.wutsi.blog.product.mapper.ProductMapper
import com.wutsi.blog.product.service.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetProductQuery(private val service: ProductService, private val mapper: ProductMapper) {
    @GetMapping("/v1/products/{id}")
    fun execute(@PathVariable id: Long): GetProductResponse =
        GetProductResponse(
            product = mapper.toProduct(
                service.findById(id)
            )
        )
}
