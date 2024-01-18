package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.BookBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.form.EBookRelocateForm
import com.wutsi.blog.app.mapper.BookMapper
import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.app.model.BookModel
import com.wutsi.blog.product.dto.ChangeBookLocationCommand
import com.wutsi.blog.product.dto.SearchBookRequest
import com.wutsi.blog.user.dto.SearchUserRequest
import org.springframework.stereotype.Service

@Service
class BookService(
    private val backend: BookBackend,
    private val userBackend: UserBackend,
    private val mapper: BookMapper,
) {
    fun get(id: Long): BookModel {
        val book = backend.get(id).book
        val authors = userBackend.search(
            SearchUserRequest(
                storeIds = listOf(book.product.storeId),
                limit = 1
            )
        ).users
        return mapper.toBookModel(book, authors[0])
    }

    fun search(request: SearchBookRequest): List<BookModel> {
        val books = backend.search(request).books
        if (books.isEmpty()) {
            return emptyList()
        }

        val storeIds = books.map { book -> book.product.storeId }.toSet()
        val authorsByStoreId = userBackend.search(
            SearchUserRequest(
                storeIds = storeIds.toList(),
                limit = storeIds.size
            )
        ).users.associateBy{ user -> user.storeId }

        return books.map { book -> mapper.toBookModel(book, authorsByStoreId[book.product.storeId] ?: User()) }
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
