package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.BookModel
import com.wutsi.blog.app.service.Moment
import com.wutsi.blog.product.dto.Book
import com.wutsi.blog.product.dto.BookSummary
import org.springframework.stereotype.Service

@Service
class BookMapper(private val productMapper: ProductMapper, private val moment: Moment) {
    fun toBookModel(book: BookSummary) = BookModel(
        id = book.id,
        product = productMapper.toProductModel(book.product, null),
        userId = book.userId,
        creationDateTime = book.creationDateTime,
        modificationDateTime = book.modificationDateTime,
        readPercentage = book.readPercentage,
        expiryDate = book.expiryDate,
        expiryDateText = book.expiryDate?.let { expiryDate -> moment.format(expiryDate) }
    )

    fun toBookModel(book: Book) = BookModel(
        id = book.id,
        product = productMapper.toProductModel(book.product, null),
        userId = book.userId,
        transactionId = book.transactionId,
        location = book.location,
        creationDateTime = book.creationDateTime,
        modificationDateTime = book.modificationDateTime,
        readPercentage = book.readPercentage,
        expiryDate = book.expiryDate,
        expiryDateText = book.expiryDate?.let { expiryDate -> moment.format(expiryDate) }
    )
}
