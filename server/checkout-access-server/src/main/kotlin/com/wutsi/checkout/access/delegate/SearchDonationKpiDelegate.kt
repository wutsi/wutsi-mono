package com.wutsi.checkout.access.delegate

import com.wutsi.checkout.access.dto.DonationKpiSummary
import com.wutsi.checkout.access.dto.SearchDonationKpiRequest
import com.wutsi.checkout.access.dto.SearchDonationKpiResponse
import com.wutsi.checkout.access.service.DonationKpiService
import com.wutsi.checkout.access.service.Mapper
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
public class SearchDonationKpiDelegate(
    private val logger: KVLogger,
    private val service: DonationKpiService,
) {
    public fun invoke(request: SearchDonationKpiRequest): SearchDonationKpiResponse {
        logger.add("request_from_date", request.fromDate)
        logger.add("request_to_date", request.toDate)
        logger.add("request_business_id", request.businessId)
        logger.add("request_aggregate", request.aggregate)

        val kpis = service.search(request)
        logger.add("response_count", kpis.size)

        return SearchDonationKpiResponse(
            kpis = kpis.map { Mapper.toKpiDonation(it) }
                .groupBy { it.date } // Aggregate by date
                .map { // Sum KPIs by dates
                    it.value.reduce { cur, acc ->
                        DonationKpiSummary(
                            cur.date,
                            cur.totalDonations + acc.totalDonations,
                            cur.totalValue + acc.totalValue,
                        )
                    }
                },
        )
    }
}
