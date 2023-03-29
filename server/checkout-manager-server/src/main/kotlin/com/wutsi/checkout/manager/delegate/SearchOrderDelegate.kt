package com.wutsi.checkout.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.manager.dto.OrderSummary
import com.wutsi.checkout.manager.dto.SearchOrderRequest
import com.wutsi.checkout.manager.dto.SearchOrderResponse
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
public class SearchOrderDelegate(
    private val logger: KVLogger,
    private val checkoutAccessApi: CheckoutAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(request: SearchOrderRequest): SearchOrderResponse {
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)
        logger.add("request_status", request.status)
        logger.add("request_created_from", request.createdFrom)
        logger.add("request_created_to", request.createdTo)
        logger.add("request_expires_to", request.expiresTo)
        logger.add("request_business_id", request.businessId)
        logger.add("request_customer_account_id", request.customerAccountId)

        val orders = checkoutAccessApi.searchOrder(
            request = com.wutsi.checkout.access.dto.SearchOrderRequest(
                customerAccountId = request.customerAccountId,
                limit = request.limit,
                offset = request.offset,
                businessId = request.businessId,
                status = request.status,
                createdTo = request.createdTo,
                createdFrom = request.createdFrom,
                expiresTo = request.expiresTo,
                productId = request.productId,
            ),
        ).orders
        return SearchOrderResponse(
            orders = orders.map {
                objectMapper.readValue(
                    objectMapper.writeValueAsString(it),
                    OrderSummary::class.java,
                )
            },
        )
    }
}
