package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.BookBackend
import com.wutsi.blog.app.form.EBookRelocateForm
import com.wutsi.blog.app.mapper.BookMapper
import com.wutsi.blog.app.model.BookModel
import com.wutsi.blog.product.dto.ChangeBookLocationCommand
import com.wutsi.blog.product.dto.SearchBookRequest
import com.wutsi.blog.product.dto.SearchProductRequest
import org.springframework.stereotype.Service

@Service
class BookService(
    private val backend: BookBackend,
    private val mapper: BookMapper,
    private val productService: ProductService
) {
    fun search(request: SearchBookRequest): List<BookModel> {
        val books = backend.search(request).books
        if (books.isEmpty()) {
            return emptyList()
        }

        val productMap = productService.search(
            SearchProductRequest(
                productIds = books.map { book -> book.productId },
                limit = books.size
            )
        ).associateBy { it.id }

        return books.mapNotNull { book ->
            productMap[book.productId]?.let { product ->
                mapper.toBookModel(book, product)
            }
        }
    }

    fun changeLocation(id: Long, form: EBookRelocateForm) {
        backend.changeLocation(
            ChangeBookLocationCommand(
                bookId = id,
                location = form.location,
                readPercentage = form.readPercentage,
            )
        )
    }

    fun get(id: Long): BookModel {
        val book = backend.get(id).book
        val product = productService.get(book.productId)
        return mapper.toBookModel(book, product)
    }
}
