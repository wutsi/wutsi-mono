package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.CheckProductAvailabilityRequest
import com.wutsi.marketplace.access.service.ProductService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
class CheckProductAvailabilityDelegate(
    private val service: ProductService,
    private val logger: KVLogger,
) {
    fun invoke(request: CheckProductAvailabilityRequest) {
        log(request)

        service.checkAvailability(request)
    }

    private fun log(request: CheckProductAvailabilityRequest) {
        var i = 1
        request.items.forEach {
            logger.add("request_product_${i}_id", it.productId)
            logger.add("request_product_${i}_quantity", it.quantity)
            i++
        }
    }
}
