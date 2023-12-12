package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.GetStoreResponse
import com.wutsi.blog.product.mapper.StoreMapper
import com.wutsi.blog.product.service.StoreService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetStoreQuery(private val service: StoreService, private val mapper: StoreMapper) {
    @GetMapping("/v1/stores/{id}")
    fun execute(@PathVariable id: String): GetStoreResponse =
        GetStoreResponse(
            store = mapper.toStore(
                service.findById(id)
            )
        )
}
