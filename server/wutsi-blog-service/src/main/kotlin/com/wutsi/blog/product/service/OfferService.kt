package com.wutsi.blog.product.service

import com.wutsi.blog.product.dao.CouponRepository
import com.wutsi.blog.product.dao.ProductRepository
import com.wutsi.blog.product.domain.CouponEntity
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.dto.SearchOfferRequest
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.blog.user.domain.UserEntity
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.jvm.optionals.getOrNull

@Service
class OfferService(
    private val userDao: UserRepository,
    private val productDao: ProductRepository,
    private val couponDao: CouponRepository,
    private val discountService: DiscountService,
) {
    fun search(request: SearchOfferRequest): List<Offer> {
        val products = productDao.findAllById(request.productIds).toList()
        if (products.isEmpty()) {
            return emptyList()
        }

        val user = request.userId?.let { userId -> userDao.findById(userId).getOrNull() }
        val discounts = user?.let {
            val coupons = couponDao.findByUserAndProductInAndExpiryDateTimeGreaterThanEqualAndTransactionNull(
                user,
                products,
                Date()
            )

            products.associate { product -> product to searchDiscounts(product, user, coupons) }
        } ?: emptyMap()
        return products.map { product ->
            toOffer(
                product,
                discounts[product]?.firstOrNull()
            )
        }
    }

    private fun searchDiscounts(product: ProductEntity, user: UserEntity, coupons: List<CouponEntity>): List<Discount> {
        val result = mutableListOf<Discount>()
        result.addAll(discountService.search(product.store, user))
        result.addAll(
            coupons.filter { coupon -> coupon.product.id == product.id }
                .map { coupon ->
                    Discount(
                        type = DiscountType.COUPON,
                        percentage = coupon.percentage,
                        expiryDate = coupon.expiryDateTime,
                        couponId = coupon.id ?: -1,
                    )
                }
        )
        return result.sortedByDescending { coupon -> coupon.percentage }
    }

    private fun toOffer(product: ProductEntity, discount: Discount?): Offer {
        val savingAmount = product.price.toDouble() * (discount?.let { discount.percentage.toDouble() / 100.0 } ?: 0.0)
        val savingPercentage = 100.0 * savingAmount / product.price
        return Offer(
            productId = product.id ?: -1,
            price = product.price - savingAmount.toLong(),
            referencePrice = product.price,
            savingAmount = savingAmount.toLong(),
            savingPercentage = savingPercentage.toInt(),
            discount = discount
        )
    }
}
