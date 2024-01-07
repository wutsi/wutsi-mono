package com.wutsi.blog.app.backend

import com.wutsi.blog.product.dto.CreateStoreCommand
import com.wutsi.blog.product.dto.GetStoreResponse
import com.wutsi.blog.product.dto.UpdateStoreDiscountsCommand
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class StoreBackend(
    private val rest: RestTemplate,

    @Value("\${wutsi.application.backend.store.endpoint}")
    private val endpoint: String,
) {
    fun get(id: String): GetStoreResponse =
        rest.getForEntity("$endpoint/$id", GetStoreResponse::class.java).body!!

    fun create(command: CreateStoreCommand) {
        rest.postForEntity("$endpoint/commands/create", command, Any::class.java)
    }

    fun updateDiscounts(command: UpdateStoreDiscountsCommand) {
        rest.postForEntity("$endpoint/commands/update-discounts", command, Any::class.java)
    }
}
