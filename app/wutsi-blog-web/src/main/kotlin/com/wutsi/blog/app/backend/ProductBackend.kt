package com.wutsi.blog.app.backend

import com.wutsi.blog.event.EventType
import com.wutsi.blog.product.dto.ImportProductCommand
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.dto.SearchProductResponse
import com.wutsi.platform.core.stream.EventStream
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ProductBackend(
    private val eventStream: EventStream,
    private val rest: RestTemplate,

    @Value("\${wutsi.application.backend.product.endpoint}")
    private val endpoint: String

) {
    fun import(request: ImportProductCommand) {
        eventStream.publish(EventType.IMPORT_PRODUCT_COMMAND, request)
    }

    fun search(request: SearchProductRequest): SearchProductResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchProductResponse::class.java).body!!
}
