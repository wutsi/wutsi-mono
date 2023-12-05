package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.GetProductResponse
import com.wutsi.blog.product.dto.Product
import com.wutsi.blog.product.service.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetProductQuery(private val service: ProductService) {
    @GetMapping("/v1/products/{id}")
    fun execute(@PathVariable id: Long): GetProductResponse {
        val product = service.findById(id)
        return GetProductResponse(
            product = Product(
                id = id,
                storeId = product.store.id ?: "",
                currency = product.store.currency,
                totalSales = product.totalSales,
                orderCount = product.orderCount,
                externalId = product.externalId,
                price = product.price,
                status = product.status,
                creationDateTime = product.creationDateTime,
                description = product.description,
                title = product.title,
                modificationDateTime = product.modificationDateTime,
                available = product.available,
                fileUrl = product.fileUrl,
                imageUrl = product.imageUrl,
            )
        )
    }
}
