package com.wutsi.blog.product.mapper

import com.wutsi.blog.product.domain.BookEntity
import com.wutsi.blog.product.dto.Book
import com.wutsi.blog.product.dto.BookSummary
import org.springframework.stereotype.Service

@Service
class BookMapper {
    fun toBook(book: BookEntity) = Book(
        id = book.id ?: -1,
        userId = book.user.id ?: -1,
        productId = book.product.id ?: -1,
        location = book.location,
        creationDateTime = book.creationDateTime,
        modificationDateTime = book.modificationDateTime,
    )

    fun toBookSummary(book: BookEntity) = BookSummary(
        id = book.id ?: -1,
        userId = book.user.id ?: -1,
        productId = book.product.id ?: -1,
        creationDateTime = book.creationDateTime,
        modificationDateTime = book.modificationDateTime,
    )
}
