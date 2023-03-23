package com.wutsi.marketplace.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.OfferSummary
import com.wutsi.marketplace.manager.dto.SearchOfferRequest
import com.wutsi.marketplace.manager.dto.SearchOfferResponse
import com.wutsi.workflow.Workflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class SearchOfferWorkflow(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) : Workflow<SearchOfferRequest, SearchOfferResponse> {
    override fun execute(request: SearchOfferRequest, context: WorkflowContext): SearchOfferResponse {
        val response = marketplaceAccessApi.searchOffer(
            request = com.wutsi.marketplace.access.dto.SearchOfferRequest(
                limit = request.limit,
                offset = request.offset,
                storeId = request.storeId,
                sortBy = request.sortBy,
                productIds = request.productIds,
                types = request.types,
            ),
        )
        return SearchOfferResponse(
            offers = response.offers.map {
                objectMapper.readValue(
                    objectMapper.writeValueAsString(it),
                    OfferSummary::class.java,
                )
            },
        )
    }
}
