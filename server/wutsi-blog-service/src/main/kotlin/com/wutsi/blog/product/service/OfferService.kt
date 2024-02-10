package com.wutsi.blog.product.service

import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.product.dao.CouponRepository
import com.wutsi.blog.product.dao.ProductRepository
import com.wutsi.blog.product.domain.CouponEntity
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.dto.SearchOfferRequest
import com.wutsi.blog.transaction.dao.WalletRepository
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.service.UserService
import org.springframework.stereotype.Service
import java.util.Date

@Service
class OfferService(
    private val productDao: ProductRepository,
    private val couponDao: CouponRepository,
    private val walletDao: WalletRepository,
    private val userService: UserService,
    private val discountService: DiscountService,
    private val exchangeRateService: ExchangeRateService,
) {
    fun search(request: SearchOfferRequest): List<Offer> {
        val products = productDao.findAllById(request.productIds).associateBy { product -> product.id }
        if (products.isEmpty()) {
            return emptyList()
        }

        val user = request.userId?.let { userId -> userService.findById(userId) }
        val discounts = user?.let {
            val coupons = couponDao.findByUserAndProductInAndExpiryDateTimeGreaterThanEqualAndTransactionNull(
                user,
                products.values.toList(),
                Date()
            )

            products.values.associate { product -> product.id to searchDiscounts(product, user, coupons) }
        } ?: emptyMap()

        val storeIds = products.values.mapNotNull { product -> product.store.id }.toSet()
        val walletIds = userService.search(
            SearchUserRequest(
                storeIds = storeIds.toList(),
                limit = storeIds.size
            )
        ).mapNotNull { merchant -> merchant.walletId }

        val wallets = walletDao.findAllById(walletIds).associateBy { it.user.id }

        return request.productIds
            .mapNotNull { productId ->
                products[productId]?.let { product ->
                    toOffer(
                        product,
                        discounts[productId]?.firstOrNull(),
                        wallets[product.store.userId],
                    )
                }
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

    private fun toOffer(product: ProductEntity, discount: Discount?, wallet: WalletEntity?): Offer {
        val savingAmount = product.price.toDouble() * (discount?.let { discount.percentage.toDouble() / 100.0 } ?: 0.0)
        val savingPercentage = 100.0 * savingAmount / product.price
        val price = product.price - savingAmount.toLong()

        val country = wallet?.country?.let { code -> Country.fromCode(code) }
        val rate = country?.let {
            exchangeRateService.getExchangeRate(product.store.currency, it.internationalCurrency)
        }

        return Offer(
            productId = product.id ?: -1,
            price = price,
            referencePrice = product.price,
            savingAmount = savingAmount.toLong(),
            savingPercentage = savingPercentage.toInt(),
            discount = discount,
            internationalCurrency = country?.internationalCurrency,
            internationalPrice = rate?.let { exchangeRateService.convert(price, it).toLong() }
        )
    }
}
