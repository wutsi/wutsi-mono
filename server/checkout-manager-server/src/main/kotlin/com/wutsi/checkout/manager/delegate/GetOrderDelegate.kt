package com.wutsi.checkout.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.manager.dto.GetOrderResponse
import com.wutsi.checkout.manager.dto.Order
import org.springframework.stereotype.Service

@Service
public class GetOrderDelegate(
    private val checkoutAccessApi: CheckoutAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(id: String): GetOrderResponse {
        val order = checkoutAccessApi.getOrder(id).order
        val json = objectMapper.writeValueAsString(order)
        return GetOrderResponse(
            order = objectMapper.readValue(json, Order::class.java),
        )

    }
}
