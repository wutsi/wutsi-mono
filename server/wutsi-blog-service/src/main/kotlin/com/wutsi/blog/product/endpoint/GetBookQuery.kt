package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.GetBookResponse
import com.wutsi.blog.product.mapper.BookMapper
import com.wutsi.blog.product.service.BookService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetBookQuery(private val service: BookService, private val mapper: BookMapper) {
    @GetMapping("/v1/books/{id}")
    fun execute(@PathVariable id: Long): GetBookResponse =
        GetBookResponse(
            book = mapper.toBook(
                book = service.findById(id)
            )
        )
}
