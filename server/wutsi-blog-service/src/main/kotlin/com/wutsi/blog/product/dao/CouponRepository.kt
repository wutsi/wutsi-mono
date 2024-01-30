package com.wutsi.blog.product.dao

import com.wutsi.blog.product.domain.CouponEntity
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.user.domain.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Date

@Repository
interface CouponRepository : CrudRepository<CouponEntity, Long> {
    fun findByUserAndProductInAndExpiryDateTimeGreaterThanEqualAndTransactionNull(
        user: UserEntity,
        product: List<ProductEntity>,
        expiryDate: Date,
    ): List<CouponEntity>
}
