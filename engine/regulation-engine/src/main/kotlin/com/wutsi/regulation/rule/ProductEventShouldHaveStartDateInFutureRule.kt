package com.wutsi.regulation.rule

import com.wutsi.enums.ProductType
import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.access.dto.Product
import com.wutsi.platform.core.error.exception.ConflictException
import java.time.OffsetDateTime
import com.wutsi.platform.core.error.Error
import com.wutsi.regulation.Rule

class ProductEventShouldHaveStartDateInFutureRule(
    private val product: Product,
) : Rule {
    override fun check() {
        if (
            product.type == ProductType.EVENT.name &&
            product.event?.starts?.isBefore(OffsetDateTime.now()) == true
        ) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.PRODUCT_EVENT_START_DATE_IN_PAST.urn,
                    data = mapOf(
                        "product-id" to product.id,
                    ),
                ),
            )
        }
    }
}
