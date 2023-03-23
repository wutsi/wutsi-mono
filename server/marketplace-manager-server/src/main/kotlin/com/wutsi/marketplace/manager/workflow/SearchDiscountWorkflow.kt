package com.wutsi.marketplace.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.DiscountSummary
import com.wutsi.marketplace.manager.dto.SearchDiscountRequest
import com.wutsi.marketplace.manager.dto.SearchDiscountResponse
import com.wutsi.workflow.Workflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class SearchDiscountWorkflow(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) : Workflow<SearchDiscountRequest, SearchDiscountResponse> {
    override fun execute(request: SearchDiscountRequest, context: WorkflowContext): SearchDiscountResponse {
        val response = marketplaceAccessApi.searchDiscount(
            request = com.wutsi.marketplace.access.dto.SearchDiscountRequest(
                storeId = request.storeId,
                date = request.date,
                productIds = request.productIds,
                discountIds = request.discountIds,
                limit = request.limit,
                offset = request.offset,
                type = request.type,
            ),
        )
        return SearchDiscountResponse(
            discounts = response.discounts.map {
                objectMapper.readValue(
                    objectMapper.writeValueAsString(it),
                    DiscountSummary::class.java,
                )
            },
        )
    }
}
