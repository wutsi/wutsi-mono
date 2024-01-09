package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.BookModel
import com.wutsi.blog.app.model.ProductModel
import com.wutsi.blog.product.dto.Book
import com.wutsi.blog.product.dto.BookSummary
import org.springframework.stereotype.Service

@Service
class BookMapper(productMapper: ProductMapper) {
    fun toBookModel(book: BookSummary, product: ProductModel) = BookModel(
        id = book.id,
        product = product,
        userId = book.userId,
        creationDateTime = book.creationDateTime,
        modificationDateTime = book.modificationDateTime,
    )

    fun toBookModel(book: Book, product: ProductModel) = BookModel(
        id = book.id,
        product = product,
        userId = book.userId,
        transactionId = book.transactionId,
        location = book.location,
        creationDateTime = book.creationDateTime,
        modificationDateTime = book.modificationDateTime,
    )
}
