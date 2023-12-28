package com.wutsi.blog.product.service

import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.dto.SearchOfferRequest
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.user.service.UserService
import org.springframework.stereotype.Service

@Service
class OfferService(
    private val userService: UserService,
    private val productService: ProductService,
    private val discountService: DiscountService,
) {
    fun search(request: SearchOfferRequest): List<Offer> {
        val user = request.userId?.let { userId -> userService.findById(userId) }
        val products = productService.searchProducts(
            SearchProductRequest(
                productIds = request.productIds,
                limit = request.productIds.size
            )
        )

        val discounts = user?.let {
            products.associate { product ->
                product.store to discountService.search(product.store, user)
            }
        } ?: emptyMap()
        return products.map { product ->
            toOffer(
                product,
                discounts[product.store]?.firstOrNull()
            )
        }
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
