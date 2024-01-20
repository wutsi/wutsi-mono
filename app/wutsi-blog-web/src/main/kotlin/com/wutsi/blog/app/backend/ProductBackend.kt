package com.wutsi.blog.app.backend

import com.wutsi.blog.event.EventType
import com.wutsi.blog.product.dto.CreateProductCommand
import com.wutsi.blog.product.dto.CreateProductResponse
import com.wutsi.blog.product.dto.GetProductResponse
import com.wutsi.blog.product.dto.ImportProductCommand
import com.wutsi.blog.product.dto.PublishProductCommand
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.dto.SearchProductResponse
import com.wutsi.blog.product.dto.UpdateProductAttributeCommand
import com.wutsi.platform.core.stream.EventStream
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ProductBackend(
    private val eventStream: EventStream,
    private val rest: RestTemplate,
    @Value("\${wutsi.application.backend.product.endpoint}") private val endpoint: String,
) {
    fun get(id: Long): GetProductResponse =
        rest.getForEntity("$endpoint/$id", GetProductResponse::class.java).body!!

    fun create(request: CreateProductCommand): CreateProductResponse =
        rest.postForEntity("$endpoint/commands/create", request, CreateProductResponse::class.java).body!!

    fun updateAttribute(request: UpdateProductAttributeCommand) {
        rest.postForEntity("$endpoint/commands/update-attribute", request, Any::class.java)
    }

    fun import(request: ImportProductCommand) {
        eventStream.publish(EventType.IMPORT_PRODUCT_COMMAND, request)
    }

    fun search(request: SearchProductRequest): SearchProductResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchProductResponse::class.java).body!!

    fun publish(request: PublishProductCommand) {
        rest.postForEntity("$endpoint/commands/publish", request, Any::class.java)
    }
}
