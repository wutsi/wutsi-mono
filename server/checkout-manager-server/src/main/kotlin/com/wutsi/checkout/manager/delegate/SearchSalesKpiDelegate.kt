package com.wutsi.checkout.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.manager.dto.SalesKpiSummary
import com.wutsi.checkout.manager.dto.SearchSalesKpiRequest
import com.wutsi.checkout.manager.dto.SearchSalesKpiResponse
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
public class SearchSalesKpiDelegate(
    private val logger: KVLogger,
    private val checkoutAccessApi: CheckoutAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(request: SearchSalesKpiRequest): SearchSalesKpiResponse {
        logger.add("request_from_date", request.fromDate)
        logger.add("request_to_date", request.toDate)
        logger.add("request_business_id", request.businessId)
        logger.add("request_product_id", request.productId)
        logger.add("request_aggregate", request.aggregate)

        val kpis = checkoutAccessApi.searchSalesKpi(
            request = com.wutsi.checkout.access.dto.SearchSalesKpiRequest(
                businessId = request.businessId,
                productId = request.productId,
                fromDate = request.fromDate,
                toDate = request.toDate,
                aggregate = request.aggregate,
            ),
        ).kpis
        return SearchSalesKpiResponse(
            kpis = kpis.map {
                objectMapper.readValue(
                    objectMapper.writeValueAsString(it),
                    SalesKpiSummary::class.java,
                )
            },
        )
    }
}
