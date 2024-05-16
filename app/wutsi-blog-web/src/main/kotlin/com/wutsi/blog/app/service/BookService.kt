package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.BookBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.form.EBookRelocateForm
import com.wutsi.blog.app.mapper.BookMapper
import com.wutsi.blog.app.model.BookModel
import com.wutsi.blog.product.dto.ChangeBookLocationCommand
import com.wutsi.blog.product.dto.SearchBookRequest
import com.wutsi.blog.product.dto.SearchCategoryRequest
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UserSummary
import org.springframework.stereotype.Service

@Service
class BookService(
    private val backend: BookBackend,
    private val userBackend: UserBackend,
    private val mapper: BookMapper,
    private val categoryService: CategoryService,
) {
    fun get(id: Long): BookModel {
        val book = backend.get(id).book
        val authors = userBackend.search(
            SearchUserRequest(
                storeIds = listOf(book.product.storeId),
                limit = 1
            )
        ).users
        return mapper.toBookModel(book, authors.firstOrNull() ?: UserSummary())
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
        ).users.associateBy { user -> user.storeId }

        val categoryIds = books.mapNotNull { book -> book.product.categoryId }.toSet().toList()
        val categoryMap = if (categoryIds.isEmpty()) {
            emptyMap()
        } else {
            categoryService.search(
                SearchCategoryRequest(
                    categoryIds = categoryIds,
                    limit = categoryIds.size
                )
            ).associateBy { it.id }
        }

        return books.map { book ->
            mapper.toBookModel(
                book,
                authorsByStoreId[book.product.storeId] ?: UserSummary(),
                book.product.categoryId?.let { categoryId -> categoryMap[categoryId] }
            )
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
}
