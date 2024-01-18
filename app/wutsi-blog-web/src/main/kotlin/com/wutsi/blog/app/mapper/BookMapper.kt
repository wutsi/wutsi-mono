package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.BookModel
import com.wutsi.blog.app.service.Moment
import com.wutsi.blog.product.dto.Book
import com.wutsi.blog.product.dto.BookSummary
import com.wutsi.blog.user.dto.UserSummary
import org.springframework.stereotype.Service

@Service
class BookMapper(
    private val productMapper: ProductMapper,
    private val userMapper: UserMapper,
    private val moment: Moment
) {
    fun toBookModel(book: BookSummary, author: UserSummary) = BookModel(
        id = book.id,
        userId = book.userId,
        creationDateTime = book.creationDateTime,
        modificationDateTime = book.modificationDateTime,
        readPercentage = book.readPercentage,
        expiryDate = book.expiryDate,
        expiryDateText = book.expiryDate?.let { expiryDate -> moment.format(expiryDate) },
        product = productMapper.toProductModel(book.product, null),
        author = userMapper.toUserModel(author),
    )

    fun toBookModel(book: Book, author: UserSummary) = BookModel(
        id = book.id,
        product = productMapper.toProductModel(book.product, null),
        userId = book.userId,
        transactionId = book.transactionId,
        location = book.location,
        creationDateTime = book.creationDateTime,
        modificationDateTime = book.modificationDateTime,
        readPercentage = book.readPercentage,
        expiryDate = book.expiryDate,
        expiryDateText = book.expiryDate?.let { expiryDate -> moment.format(expiryDate) },
        author = userMapper.toUserModel(author),
    )
}
