package com.wutsi.marketplace.manager.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.manager.dto.Discount
import com.wutsi.marketplace.manager.dto.GetDiscountResponse
import com.wutsi.workflow.Workflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
class GetDiscountWorkflow(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val objectMapper: ObjectMapper,
) : Workflow<Long, GetDiscountResponse> {
    override fun execute(discountId: Long, context: WorkflowContext): GetDiscountResponse {
        val discount = marketplaceAccessApi.getDiscount(discountId).discount
        return GetDiscountResponse(
            discount = objectMapper.readValue(
                objectMapper.writeValueAsString(discount),
                Discount::class.java,
            ),
        )
    }
}
