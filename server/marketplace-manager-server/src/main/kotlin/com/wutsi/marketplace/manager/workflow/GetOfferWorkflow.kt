package com.wutsi.marketplace.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.GetOfferResponse
import com.wutsi.marketplace.manager.dto.Offer
import com.wutsi.workflow.Workflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class GetOfferWorkflow(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) : Workflow<Long, GetOfferResponse> {
    override fun execute(offerId: Long, context: WorkflowContext): GetOfferResponse {
        val offer = marketplaceAccessApi.getOffer(offerId).offer
        return GetOfferResponse(
            offer = objectMapper.readValue(
                objectMapper.writeValueAsString(offer),
                Offer::class.java,
            ),
        )
    }
}
