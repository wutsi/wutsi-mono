package com.wutsi.marketplace.access.service

import com.wutsi.enums.DiscountType
import com.wutsi.enums.ProductStatus
import com.wutsi.marketplace.access.dto.Offer
import com.wutsi.marketplace.access.dto.OfferPrice
import com.wutsi.marketplace.access.dto.OfferSummary
import com.wutsi.marketplace.access.dto.SearchDiscountRequest
import com.wutsi.marketplace.access.dto.SearchOfferRequest
import com.wutsi.marketplace.access.dto.SearchProductRequest
import com.wutsi.marketplace.access.entity.DiscountEntity
import com.wutsi.marketplace.access.entity.ProductEntity
import com.wutsi.marketplace.access.error.ErrorURN
import com.wutsi.marketplace.access.service.filter.ExpiredEventFilter
import com.wutsi.marketplace.access.service.filter.OfferSetFilter
import com.wutsi.marketplace.access.service.filter.OutOfStockProductFilter
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId

@Service
class OfferService(
    private val productService: ProductService,
    private val discountService: DiscountService,
) {
    private val filters = OfferSetFilter(
        filters = listOf(
            ExpiredEventFilter(),
            OutOfStockProductFilter(), // IMPORTANT: Should be the last filter
        ),
    )

    fun search(request: SearchOfferRequest, language: String?): List<OfferSummary> {
        val products = productService.search(
            request = SearchProductRequest(
                status = ProductStatus.PUBLISHED.name,
                storeId = request.storeId,
                productIds = request.productIds,
                types = request.types,
                sortBy = request.sortBy,
                limit = request.limit,
                offset = request.offset,
            ),
        )
        val prices = searchPrices(products).associateBy { it.productId }

        val offers = products.mapNotNull {
            val price = prices[it.id]
            if (price == null) {
                null
            } else {
                OfferSummary(
                    product = productService.toProductSummary(it, language),
                    price = price,
                )
            }
        }
        return filters.filter(offers)
    }

    fun findById(id: Long, language: String?): Offer {
        val product = productService.findById(id)
        if (product.status != ProductStatus.PUBLISHED) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.PRODUCT_NOT_PUBLISHED.urn,
                ),
            )
        }

        val prices = searchPrices(listOf(product))
        if (prices.isEmpty()) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.PRICE_NOT_FOUND.urn,
                ),
            )
        }

        return Offer(
            product = productService.toProduct(product, language),
            price = prices[0],
        )
    }

    private fun searchPrices(products: List<ProductEntity>): List<OfferPrice> {
        if (products.isEmpty()) {
            return emptyList()
        }

        // Search discounts
        val discounts = discountService.search(
            request = SearchDiscountRequest(
                storeId = products[0].store.id ?: -1,
                productIds = products.mapNotNull { it.id },
                date = LocalDate.now(ZoneId.of("UTC")),
                type = DiscountType.SALES.name,
                limit = 100,
            ),
        )

        // Compute price with savings
        val prices = mutableListOf<OfferPrice>()
        for (discount in discounts) {
            for (product in products) {
                if (canApply(product, discount)) {
                    prices.add(computePrice(product, discount))
                }
            }
        }
        val result = prices.groupBy { it.productId }
            .map { it.value.reduce { acc, cur -> if (acc.savings > cur.savings) acc else cur } } // Select the price with highest savings
            .toMutableList()

        // Add price without savings
        val productIds = result.map { it.productId }
        products.filter { !productIds.contains(it.id) }
            .forEach {
                result.add(computePrice(it))
            }

        return result
    }

    private fun canApply(product: ProductEntity, discount: DiscountEntity): Boolean =
        discount.allProducts || discount.products.contains(product)

    private fun computePrice(product: ProductEntity): OfferPrice =
        OfferPrice(
            productId = product.id ?: -1,
            price = product.price ?: 0,
        )

    private fun computePrice(product: ProductEntity, discount: DiscountEntity): OfferPrice {
        val price = product.price ?: 0L
        if (price == 0L) {
            return OfferPrice(
                productId = product.id ?: -1,
                price = price,
            )
        }

        val priceWidthSavings = roundMoney(price - (price * discount.rate) / 100, product.currency)
        val savings = price - priceWidthSavings
        return OfferPrice(
            productId = product.id ?: -1,
            discountId = discount.id,
            savings = savings,
            savingsPercentage = (savings * 100L / price).toInt(),
            referencePrice = price,
            price = price - savings,
            expires = discount.ends?.let { OffsetDateTime.ofInstant(it.toInstant(), ZoneId.of("UTC")) },
        )
    }

    private fun roundMoney(amount: Long, currency: String): Long =
        if (currency == "XAF" || currency == "XOF") {
            (amount / 5) * 5
        } else {
            amount
        }
}
