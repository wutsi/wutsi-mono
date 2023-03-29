package com.wutsi.regulation.rule

import com.wutsi.checkout.access.dto.Order
import com.wutsi.enums.OrderStatus
import com.wutsi.error.ErrorURN
import com.wutsi.platform.core.error.exception.ConflictException
import java.time.OffsetDateTime
import java.time.ZoneOffset
import com.wutsi.platform.core.error.Error
import com.wutsi.regulation.Rule

class OrderShouldNotBeExpiredRule(
    private val order: Order,
) : Rule {
    override fun check() {
        if (order.status == OrderStatus.EXPIRED.name || order.expires.isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.ORDER_EXPIRED.urn,
                    data = mapOf(
                        "order-id" to order.id,
                    ),
                ),
            )
        }
    }
}
