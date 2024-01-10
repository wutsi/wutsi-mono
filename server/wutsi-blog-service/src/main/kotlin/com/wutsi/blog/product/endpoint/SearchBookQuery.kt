package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.SearchBookRequest
import com.wutsi.blog.product.dto.SearchBookResponse
import com.wutsi.blog.product.mapper.BookMapper
import com.wutsi.blog.product.service.BookService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchBookQuery(private val service: BookService, private val mapper: BookMapper) {
    @PostMapping("/v1/books/queries/search")
    fun execute(@Valid @RequestBody request: SearchBookRequest): SearchBookResponse =
        SearchBookResponse(
            books = service.search(request).map { book -> mapper.toBookSummary(book) }
        )
}
