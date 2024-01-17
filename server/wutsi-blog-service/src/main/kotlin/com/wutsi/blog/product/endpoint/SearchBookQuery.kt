package com.wutsi.blog.product.endpoint

import com.wutsi.blog.product.dto.SearchBookRequest
import com.wutsi.blog.product.dto.SearchBookResponse
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.mapper.BookMapper
import com.wutsi.blog.product.service.BookService
import com.wutsi.blog.product.service.ProductService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchBookQuery(
    private val service: BookService,
    private val productService: ProductService,
    private val mapper: BookMapper
) {
    @PostMapping("/v1/books/queries/search")
    fun execute(@Valid @RequestBody request: SearchBookRequest): SearchBookResponse {
        val books = service.search(request)
        val expiryDates = service.computeExpiryDates(books)
        val productMap = productService.searchProducts(
            SearchProductRequest(
                productIds = books.mapNotNull { book -> book.product.id },
                limit = books.size
            )
        ).associateBy { it.id }

        return SearchBookResponse(
            books = books.map { book ->
                mapper.toBookSummary(
                    book = book,
                    product = productMap[book.product.id ?: -1],
                    expiryDate = expiryDates[book]
                )
            }
        )
    }
}
