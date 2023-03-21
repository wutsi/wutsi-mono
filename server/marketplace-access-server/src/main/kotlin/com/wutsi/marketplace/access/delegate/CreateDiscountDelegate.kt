package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.CreateDiscountRequest
import com.wutsi.marketplace.access.dto.CreateDiscountResponse
import com.wutsi.marketplace.access.service.DiscountService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class CreateDiscountDelegate(
    private val logger: KVLogger,
    private val service: DiscountService,
) {
    @Transactional
    public fun invoke(request: CreateDiscountRequest): CreateDiscountResponse {
        logger.add("request_name", request.name)
        logger.add("request_all_products", request.allProducts)
        logger.add("request_starts", request.starts)
        logger.add("request_ends", request.ends)
        logger.add("request_rate", request.rate)
        logger.add("request_store_id", request.storeId)

        val discount = service.create(request)
        logger.add("discount_id", discount.id)

        return CreateDiscountResponse(
            discountId = discount.id ?: -1,
        )
    }
}
