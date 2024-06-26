package com.wutsi.blog.product.service

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.product.dao.PageRepository
import com.wutsi.blog.product.domain.PageEntity
import com.wutsi.blog.product.service.discount.DonationDiscountRule
import com.wutsi.blog.transaction.dao.TransactionRepository
import com.wutsi.blog.user.service.UserService
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service

@Service
class PageService(
    private val dao: PageRepository,
    private val productService: ProductService,
    private val transactionDao: TransactionRepository,
    private val logger: KVLogger,
    private val userService: UserService,
    private val em: EntityManager,
    private val donationDiscountRule: DonationDiscountRule,
) {
    fun find(productId: Long, number: Int): PageEntity {
        val product = productService.findById(productId)
        if (number < 0 || number > (product.numberOfPages ?: 0)) {
            throw notFound(number)
        }
        return dao.findByProductAndNumber(product, number).firstOrNull()
            ?: throw notFound(number)
    }

    private fun notFound(number: Int) = NotFoundException(
        error = Error(
            code = ErrorCode.PAGE_NOT_FOUND,
            parameter = Parameter(
                name = "number",
                value = number
            )
        )
    )
}
