package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.GetStoreResponse
import com.wutsi.blog.product.dto.Store
import com.wutsi.blog.product.service.StoreService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetStoreQuery(private val service: StoreService) {
    @GetMapping("/v1/stores/{id}")
    fun execute(@PathVariable id: String): GetStoreResponse {
        val store = service.findById(id)
        return GetStoreResponse(
            store = Store(
                id = id,
                userId = store.userId,
                currency = store.currency,
                productCount = store.productCount,
                orderCount = store.orderCount,
                totalSales = store.totalSales,
                creationDateTime = store.creationDateTime,
                modificationDateTime = store.modificationDateTime,
            )
        )
    }
}
