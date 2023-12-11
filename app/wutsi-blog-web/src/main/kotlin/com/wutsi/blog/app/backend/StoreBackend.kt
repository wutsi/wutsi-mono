package com.wutsi.blog.app.backend

import com.wutsi.blog.product.dto.CreateStoreCommand
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class StoreBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.store.endpoint}")
    private lateinit var endpoint: String

    fun create(command: CreateStoreCommand) {
        rest.postForEntity("$endpoint/commands/create", command, Any::class.java)
    }
}
