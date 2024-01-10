package com.wutsi.blog.app.backend

import com.wutsi.blog.event.EventType
import com.wutsi.blog.product.dto.ChangeBookLocationCommand
import com.wutsi.blog.product.dto.GetBookResponse
import com.wutsi.blog.product.dto.SearchBookRequest
import com.wutsi.blog.product.dto.SearchBookResponse
import com.wutsi.platform.core.stream.EventStream
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class BookBackend(
    private val rest: RestTemplate,
    private val eventStream: EventStream,
    @Value("\${wutsi.application.backend.book.endpoint}") private val endpoint: String
) {
    fun get(id: Long): GetBookResponse =
        rest.getForEntity("$endpoint/$id", GetBookResponse::class.java).body!!

    fun search(request: SearchBookRequest): SearchBookResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchBookResponse::class.java).body!!

    fun changeLocation(request: ChangeBookLocationCommand) {
        eventStream.publish(EventType.CHANGE_BOOK_LOCATION_COMMAND, request)
    }
}
