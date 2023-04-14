package com.wutsi.checkout.manager.`delegate`

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.manager.dto.DonationKpiSummary
import com.wutsi.checkout.manager.dto.SearchDonationKpiRequest
import com.wutsi.checkout.manager.dto.SearchDonationKpiResponse
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
public class SearchDonationKpiDelegate(
    private val logger: KVLogger,
    private val checkoutAccessApi: CheckoutAccessApi,
    private val objectMapper: ObjectMapper,
) {
    public fun invoke(request: SearchDonationKpiRequest): SearchDonationKpiResponse {
        logger.add("request_from_date", request.fromDate)
        logger.add("request_to_date", request.toDate)
        logger.add("request_business_id", request.businessId)
        logger.add("request_aggregate", request.aggregate)

        val orders = checkoutAccessApi.searchDonationKpi(
            request = com.wutsi.checkout.access.dto.SearchDonationKpiRequest(
                businessId = request.businessId,
                fromDate = request.fromDate,
                toDate = request.toDate,
                aggregate = request.aggregate,
            ),
        ).kpis
        return SearchDonationKpiResponse(
            kpis = orders.map {
                objectMapper.readValue(
                    objectMapper.writeValueAsString(it),
                    DonationKpiSummary::class.java,
                )
            },
        )
    }
}
