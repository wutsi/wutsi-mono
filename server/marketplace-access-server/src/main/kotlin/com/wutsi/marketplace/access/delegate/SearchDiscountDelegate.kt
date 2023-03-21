package com.wutsi.marketplace.access.delegate

import com.wutsi.marketplace.access.dto.SearchDiscountRequest
import com.wutsi.marketplace.access.dto.SearchDiscountResponse
import com.wutsi.marketplace.access.service.DiscountService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
public class SearchDiscountDelegate(
    private val logger: KVLogger,
    private val service: DiscountService,
) {
    public fun invoke(request: SearchDiscountRequest): SearchDiscountResponse {
        logger.add("request_date", request.date)
        logger.add("request_product_ids", request.productIds)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)
        logger.add("request_store_id", request.storeId)

        val discounts = service.search(request)
        logger.add("response_count", discounts.size)

        return SearchDiscountResponse(
            discounts = discounts.map { service.toDiscountSummary(it) },
        )
    }
}
