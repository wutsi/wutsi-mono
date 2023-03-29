package com.wutsi.checkout.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.manager.dto.Business
import com.wutsi.checkout.manager.dto.GetBusinessResponse
import org.springframework.stereotype.Service

@Service
public class GetBusinessDelegate(
    private val objectMapper: ObjectMapper,
    private val checkoutAccessApi: CheckoutAccessApi,
) {
    public fun invoke(id: Long): GetBusinessResponse {
        val business = checkoutAccessApi.getBusiness(id).business
        return GetBusinessResponse(
            business = objectMapper.readValue(
                objectMapper.writeValueAsString(business),
                Business::class.java,
            ),
        )
    }
}
