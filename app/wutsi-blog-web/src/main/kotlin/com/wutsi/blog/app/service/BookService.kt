package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.BookBackend
import com.wutsi.blog.app.form.EBookRelocateForm
import com.wutsi.blog.app.mapper.BookMapper
import com.wutsi.blog.app.model.BookModel
import com.wutsi.blog.product.dto.ChangeBookLocationCommand
import com.wutsi.blog.product.dto.SearchBookRequest
import org.springframework.stereotype.Service

@Service
class BookService(
    private val backend: BookBackend,
    private val mapper: BookMapper,
) {
    fun get(id: Long): BookModel {
        val book = backend.get(id).book
        return mapper.toBookModel(book)
    }

    fun search(request: SearchBookRequest): List<BookModel> {
        val books = backend.search(request).books
        if (books.isEmpty()) {
            return emptyList()
        }

        return books.map { book -> mapper.toBookModel(book) }
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
}
