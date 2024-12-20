package com.wutsi.blog.product.service

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.product.dao.BookRepository
import com.wutsi.blog.product.dao.SearchBookQueryBuilder
import com.wutsi.blog.product.domain.BookEntity
import com.wutsi.blog.product.dto.ChangeBookLocationCommand
import com.wutsi.blog.product.dto.CreateBookCommand
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.dto.ProductType
import com.wutsi.blog.product.dto.SearchBookRequest
import com.wutsi.blog.product.service.discount.DonationDiscountRule
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.Predicates
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.MimeTypes
import com.wutsi.platform.payment.core.Status
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class BookService(
    private val dao: BookRepository,
    private val transactionDao: TransactionRepository,
    private val logger: KVLogger,
    private val userService: UserService,
    private val em: EntityManager,
    private val donationDiscountRule: DonationDiscountRule,
) {
    companion object {
        private val CONTENT_TYPES = listOf(MimeTypes.EPUB, MimeTypes.CBZ)
        private val PRODUCT_TYPES = listOf(ProductType.COMICS, ProductType.EBOOK, ProductType.NEWSPAPER)
    }

    fun findById(id: Long): BookEntity = dao
        .findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorCode.BOOK_NOT_FOUND,
                        parameter = Parameter(
                            name = "id",
                            value = id
                        )
                    )
                )
            }

    fun computeExpiryDate(book: BookEntity): Date? {
        if (book.transaction.amount == 0L && book.transaction.discountType == DiscountType.DONATION) {
            val discount = donationDiscountRule.doApply(book.product.store, book.user)
            return discount?.expiryDate
        }
        return null
    }

    fun computeExpiryDates(books: List<BookEntity>): Map<BookEntity, Date?> {
        val result = mutableMapOf<BookEntity, Date?>()
        books.forEach { book ->
            result[book] = computeExpiryDate(book)
        }
        return result
    }

    fun search(request: SearchBookRequest): List<BookEntity> {
        logger.add("request_user_id", request.userId)
        logger.add("request_product_ids", request.productIds)
        logger.add("request_book_ids", request.bookIds)
        logger.add("request_transaction_id", request.transactionId)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)

        val builder = SearchBookQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, BookEntity::class.java)
        Predicates.setParameters(query, params)
        return query.resultList as List<BookEntity>
    }

    @Transactional
    fun createBook(command: CreateBookCommand): BookEntity? {
        logger.add("command", "CreateBookCommand")
        logger.add("command_transaction_id", command.transactionId)

        val tx = transactionDao.findById(command.transactionId).get()
        return createBook(tx)
    }

    fun createBook(tx: TransactionEntity): BookEntity? {
        if (!canCreateBookFrom(tx)) {
            return null
        }

        val book = dao.save(
            BookEntity(
                product = tx.product!!,
                user = resolveUser(tx)!!,
                transaction = tx,
            )
        )
        logger.add("book_id", book.id)
        return book
    }

    @Transactional
    fun changeLocation(command: ChangeBookLocationCommand) {
        logger.add("command", "ChangeBookLocationCommand")
        logger.add("command_book_id", command.bookId)
        logger.add("command_location", command.location)

        val book = findById(command.bookId)
        book.location = command.location
        book.readPercentage = command.readPercentage
        book.modificationDateTime = Date()
        dao.save(book)
    }

    private fun canCreateBookFrom(tx: TransactionEntity): Boolean =
        tx.type == TransactionType.CHARGE &&
                tx.status == Status.SUCCESSFUL &&
                tx.product != null &&
            PRODUCT_TYPES.contains(tx.product.type) &&
                tx.product.fileContentType != null &&
        CONTENT_TYPES.contains(tx.product.fileContentType)

    private fun resolveUser(tx: TransactionEntity): UserEntity? =
        tx.user ?: tx.email?.ifEmpty { null }?.let {
            userService.findByEmailOrCreate(tx.email!!)
        }
}
