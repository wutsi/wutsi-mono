package com.wutsi.regulation.rule

import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.access.dto.Product
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.regulation.Rule

class ProductShouldHavePriceRule(
    private val product: Product,
) : Rule {
    override fun check() {
        if (product.price == null || product.price!! <= 0) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.PRODUCT_PRICE_MISSING.urn,
                    data = mapOf(
                        "product-id" to product.id,
                    ),
                ),
            )
        }
    }
}
