package com.wutsi.blog.product.mapper

import com.wutsi.blog.product.domain.BookEntity
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.Book
import com.wutsi.blog.product.dto.BookSummary
import com.wutsi.blog.product.dto.ProductSummary
import org.springframework.stereotype.Service
import java.util.Date

@Service
class BookMapper(private val productMapper: ProductMapper) {
    fun toBook(book: BookEntity, expiryDate: Date?) = Book(
        id = book.id ?: -1,
        userId = book.user.id ?: -1,
        transactionId = book.transaction.id ?: "",
        location = book.location,
        readPercentage = book.readPercentage,
        creationDateTime = book.creationDateTime,
        modificationDateTime = book.modificationDateTime,
        product = productMapper.toProduct(book.product),
        expiryDate = expiryDate
    )

    fun toBookSummary(book: BookEntity, product: ProductEntity?, expiryDate: Date?) = BookSummary(
        id = book.id ?: -1,
        userId = book.user.id ?: -1,
        transactionId = book.transaction.id ?: "",
        creationDateTime = book.creationDateTime,
        modificationDateTime = book.modificationDateTime,
        readPercentage = book.readPercentage,
        expiryDate = expiryDate,
        product = product?.let { productMapper.toProductSummary(product) }
            ?: ProductSummary(id = product?.id ?: -1),
    )
}
