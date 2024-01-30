package com.wutsi.blog.product.service

import com.wutsi.blog.error.ErrorCode
import com.wutsi.blog.product.dao.CouponRepository
import com.wutsi.blog.product.domain.CouponEntity
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.exception.CouponException
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.exception.NotFoundException
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class CouponService(
    private val dao: CouponRepository,
) {
    fun findById(id: Long): CouponEntity =
        dao.findById(id).orElseThrow {
            NotFoundException(
                error = Error(
                    code = ErrorCode.COUPON_NOT_FOUND,
                    parameter = Parameter(
                        value = id
                    )
                )
            )
        }

    fun use(tx: TransactionEntity) {
        val coupon = tx.coupon ?: return

        if (coupon.transaction != null) {
            throw couponException(ErrorCode.COUPON_ALREADY_USED, coupon)
        } else if (coupon.expiryDateTime.before(Date())) {
            throw couponException(ErrorCode.COUPON_EXPIRED, coupon)
        } else if (coupon.product.id != tx.product?.id) {
            throw couponException(ErrorCode.COUPON_PRODUCT_MISMATCH, coupon)
        } else if (coupon.user.id != tx.user?.id) {
            throw couponException(ErrorCode.COUPON_USER_MISMATCH, coupon)
        }

        coupon.transaction = tx
        dao.save(coupon)
    }

    @Transactional
    fun onTransactionFailed(tx: TransactionEntity) {
        val coupon = tx.coupon ?: return

        if (coupon.transaction == tx) {
            coupon.transaction = null
            dao.save(coupon)
        }
    }

    @Transactional
    fun create(user: UserEntity, product: ProductEntity, percentage: Int): CouponEntity =
        dao.save(
            CouponEntity(
                user = user,
                product = product,
                percentage = percentage,
                expiryDateTime = DateUtils.addDays(Date(), 5)
            )
        )

    private fun couponException(code: String, coupon: CouponEntity) = CouponException(
        error = Error(
            code = code,
            parameter = Parameter(
                name = "couponId",
                value = coupon.id
            ),
        ),
    )
}
